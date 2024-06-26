package com.dam.wewiza_front.viewModels

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.services.RetrofitServiceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListScreenViewModel : ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()
    val productsList = mutableStateOf(mutableListOf<Product>())
    private val auth = FirebaseAuth.getInstance()
    private var profile: Profile? = null
    private val db = FirebaseFirestore.getInstance()
    private val profilesCollection = db.collection("profiles")
    val sharedViewModel = SharedViewModel.instance


    init {
        viewModelScope.launch(Dispatchers.IO) {
            setProfile()
            clearData()
        }
    }

    private suspend fun clearData() {
        while (auth.currentUser != null) {
            delay(100)
        }
        sharedViewModel.resetLocalData()
    }

    private suspend fun setProfile() {
        while (sharedViewModel.getLocalProfile().value == null) {
            delay(100) // Espera 100 milisegundos antes de verificar de nuevo
        }
        profile = sharedViewModel.getLocalProfile().value

    }

    private fun updateUserList(listUuid: String, updatedProducts: List<Product>) {
        if (profile == null) {
            profile = sharedViewModel.getLocalProfile().value
        }
        profile?.let { userProfile ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Encontrar la lista de compras correspondiente en el perfil del usuario
                    val shoppingList = userProfile.shoppingListsList?.find { it.uuid == listUuid }
                    if (shoppingList != null) {
                        // Actualizar los productos de la lista de compras
                        shoppingList.products.clear() // Limpiar la lista existente
                        shoppingList.products.addAll(updatedProducts.map { it.uuid }) // Agregar los UUID de los nuevos productos

                        userProfile.email?.let { updateProfileInFirebaseByEmail(it, userProfile) }

                        Log.d("updateUserList", "Lists updated for list UUID: $listUuid")
                    } else {
                        Log.d(
                            "updateUserList",
                            "Shopping list with UUID $listUuid not found in user profile"
                        )
                    }
                } catch (e: Exception) {
                    Log.d("updateUserList", "updateUserList: ${e.message}")
                }
            }
        } ?: Log.d("updateUserList", "User profile not found")
    }

    private suspend fun updateProfileInFirebaseByEmail(userEmail: String, userProfile: Profile) {
        try {
            val querySnapshot = profilesCollection.whereEqualTo("email", userEmail).get().await()
            if (!querySnapshot.isEmpty) {
                // Se espera que haya solo un documento con el correo electrónico único
                val profileDocument = querySnapshot.documents[0]
                profileDocument.reference.set(userProfile).await()
                Log.d("updateUserList", "Profile updated in Firebase for email: $userEmail")
            } else {
                Log.d("updateUserList", "User profile not found in Firebase for email: $userEmail")
            }
        } catch (e: Exception) {
            Log.d("updateUserList", "Failed to update profile in Firebase: ${e.message}")
        }
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

    fun deleteProduct(product: Product) {

        db.collection("profiles").document(auth.currentUser!!.email.toString()).get()
            .addOnSuccessListener { document ->
                val profile = document.toObject(Profile::class.java)
                val shoppingList =
                    profile?.shoppingListsList?.find { it.uuid == sharedViewModel.selectedList.value?.uuid }
                shoppingList?.products?.remove(product.uuid)
                db.collection("profiles").document(auth.currentUser!!.email.toString())
                    .set(profile!!)
            }
    }

    fun navigateToProductDetailsScreen(navController: NavController) {
        navController.navigate(AppScreens.ProductDetailsScreen.route)
    }

    private fun retrieveShoppingListFromProfile(profileEmail: String, listUuid: String, callback: (ShoppingList?) -> Unit) {
        val profileRef = db.collection("profiles").document(profileEmail)
        var shoppingList: ShoppingList? = null

        profileRef.get().addOnSuccessListener { document ->
            val profile = document.toObject(Profile::class.java)

            if (profile?.shoppingListsList?.isNotEmpty() == true) {
                val wantedList = profile.shoppingListsList!!.find { it.uuid == listUuid }
                callback(wantedList)
            } else {
                callback(null)
            }
        }.addOnFailureListener { exception ->
            Log.e("RetrieveListFromFirebase", "Error retrieving profile", exception)
            callback(null)
        }
    }

    fun navigateToSuggestionScreen(navController: NavHostController, uuid: String) {
        viewModelScope.launch {
            retrieveShoppingListFromProfile(auth.currentUser!!.email!!, uuid) { selectedList ->
                selectedList?.let { // Verifica si la lista seleccionada no es nula
                    Log.d("ListsScreenViewModel", "selectedList: $selectedList")
                    sharedViewModel.setSelectedList(selectedList)
                    navController.navigate(AppScreens.SuggestionScreen.route)
                } ?: run {

                    Log.e("ListsScreenViewModel", "Error: No se pudo recuperar la lista seleccionada")
                }
            }
        }
    }


}

