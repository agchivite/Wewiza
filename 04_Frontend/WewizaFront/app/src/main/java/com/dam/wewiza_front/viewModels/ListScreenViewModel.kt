package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.screens.sharedViewModel
import com.dam.wewiza_front.services.RetrofitServiceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListScreenViewModel : ViewModel() {

    private val selectedList = sharedViewModel.getSelectedList()
    private val service = RetrofitServiceFactory.makeRetrofitService()
    val productsList = mutableStateOf(mutableListOf<Product>())
    private val auth = FirebaseAuth.getInstance()
    private var profile: Profile? = null
    private val db = FirebaseFirestore.getInstance()
    private val profilesCollection = db.collection("profiles")



    private var previousListSize = selectedList.value?.products?.size ?: 0
    init {
        viewModelScope.launch(Dispatchers.IO){
            while (sharedViewModel.getSelectedList().value == null) {
                // Wait 100 milliseconds before checking again
                delay(100)
            }
            getProductsFromList()
        }

        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                if (productsList.value.size != previousListSize) {
                    previousListSize = productsList.value.size
                    updateUserList(selectedList.value?.uuid ?: "", productsList.value)
                }
                delay(100)  // Espera 100 milisegundos antes de verificar de nuevo
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            setProfileAndLists()
            clearData()
        }

    }

    private suspend fun clearData() {
        while (auth.currentUser != null) {
            delay(100)
        }
        sharedViewModel.resetLocalData()
    }

    private suspend fun setProfileAndLists() {
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
                        Log.d("updateUserList", "Shopping list with UUID $listUuid not found in user profile")
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



    private fun getProductsFromList() {
        viewModelScope.launch {
            if (selectedList.value != null) {
                for (productId in selectedList.value!!.products) {
                    productsList.value.add(service.getProductById(productId))
                }
            }
        }
    }

}

