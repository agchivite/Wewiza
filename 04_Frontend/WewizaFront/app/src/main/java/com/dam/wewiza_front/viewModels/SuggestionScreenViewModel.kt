package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.services.RetrofitServiceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class SuggestionScreenViewModel: ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val sharedViewModel = SharedViewModel.instance
    private val service = RetrofitServiceFactory.makeRetrofitService()
    var suggestions = mutableStateOf(mutableMapOf<String, List<Product>>())
    private val choosenSuggestionsList = mutableStateOf(mutableMapOf<String, String>())


   private val ioDispatcher: CoroutineDispatcher = newFixedThreadPoolContext(2, "IOPool")

    suspend fun getSuggestions(wantedMarkets: List<String>, products: List<String>): Map<String, List<Product>> {
        val baseUrl = "https://wewiza.ddns.net/suggest/id/"
        return withContext(ioDispatcher) {
            try {
                val resultMap = mutableMapOf<String, List<Product>>()

                for (uuid in products) {
                    val url = buildUrl(baseUrl, uuid, wantedMarkets)
                    val suggestedProducts = async { service.getSuggestions(url) }
                    resultMap[uuid] = suggestedProducts.await()
                }

                withContext(Dispatchers.Main) {
                    Log.d("Suggestions", "getSuggestions: $resultMap")
                    suggestions.value = resultMap.toMutableMap()
                }

                resultMap
            } catch (e: Exception) {
                Log.e("SuggestionScreenViewModel", "getSuggestions: ${e.message}")
                emptyMap<String, List<Product>>()
            }
        }
    }


    private fun buildUrl(baseUrl: String, uuid: String, wantedMarkets: List<String>): String {
        val urlBuilder = StringBuilder(baseUrl)
        urlBuilder.append(uuid)

        if (wantedMarkets.isNotEmpty()) {
            urlBuilder.append("?filter_markets=").append(wantedMarkets[0])
            for (i in 1 until wantedMarkets.size) {
                urlBuilder.append(",").append(wantedMarkets[i])
            }
        }

        return urlBuilder.toString()
    }

    suspend fun getProductsFromList(selectedProductsIds: ShoppingList?): List<Product> {
        var updatedProducts = listOf<Product>()
        if (selectedProductsIds != null && selectedProductsIds.products.isNotEmpty()) {
            updatedProducts = selectedProductsIds.products.map { service.getProductById(it) }
            Log.d("getProductsFromList", updatedProducts.toString())
            while (updatedProducts.size != selectedProductsIds.products.size) {
                delay(100)
            }
        }

        return updatedProducts
    }

    fun deleteProductFromSuggestions(product: Product, uuid: String) {
        val updatedSuggestions = suggestions.value.toMutableMap()
        updatedSuggestions[uuid] = updatedSuggestions[uuid]?.filter { it.uuid != product.uuid } ?: emptyList()
        suggestions.value = updatedSuggestions
    }

    fun addProductToChoosenProductsList(product: Product, listUuid: String) {
        choosenSuggestionsList.value[listUuid] = product.uuid

        //remove rest of the suggestions
        val updatedSuggestions = suggestions.value.toMutableMap()
        updatedSuggestions[listUuid] = updatedSuggestions[listUuid]?.filter { it.uuid == product.uuid} ?: emptyList()
        suggestions.value = updatedSuggestions
    }

    fun acceptSuggestions(shoppingListUUID: String) {
        updateSelectedListWithSuggestions(shoppingListUUID)
    }


    private fun updateSelectedListWithSuggestions(shoppingListUUID: String) {
        try {
            val userEmail = auth.currentUser?.email ?: throw Exception("User not authenticated")

            db.collection("profiles").document(userEmail).get()
                .addOnSuccessListener { document ->
                    val profile = document.toObject(Profile::class.java)
                    val shoppingList = profile?.shoppingListsList?.find { it.uuid == shoppingListUUID }

                    if (shoppingList != null) {
                        // Reemplaza los UUID originales por los sugeridos
                        val updatedProducts = shoppingList.products.map { originalUuid ->
                            choosenSuggestionsList.value[originalUuid] ?: originalUuid
                        }

                        // Actualiza la lista de compras en el perfil
                        shoppingList.products = updatedProducts as MutableList<String>

                        // Guarda el perfil actualizado en Firebase
                        db.collection("profiles").document(userEmail)
                            .set(profile)
                            .addOnSuccessListener {
                                Log.d("UpdateSuggestions", "Successfully updated profile in Firebase")
                            }
                            .addOnFailureListener { e ->
                                Log.d("UpdateSuggestions", "Failed to update profile in Firebase: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("UpdateSuggestions", "Failed to get profile from Firebase: ${e.message}")
                }
        } catch (e: Exception) {
            Log.d("UpdateSuggestions", "Failed to update profile in Firebase: ${e.message}")
        }
    }

    fun navigateToMyListsScreen(navController: NavController) {
        navController.navigate(AppScreens.MyListScreen.route)
    }


}