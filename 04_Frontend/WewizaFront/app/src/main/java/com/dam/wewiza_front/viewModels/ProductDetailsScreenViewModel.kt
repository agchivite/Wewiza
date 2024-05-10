package com.dam.wewiza_front.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.services.RetrofitServiceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductDetailsScreenViewModel : ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()
    private val sharedViewModel = SharedViewModel.instance
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var profile: Profile? = null
    private val profilesCollection = db.collection("profiles")


    fun getProductHistoryDetails(): MutableList<Product> {
        if (sharedViewModel.getHistoryDetails().isNotEmpty()) {
            return sharedViewModel.getHistoryDetails()
        }
        return mutableListOf()
    }


    fun likeProduct(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.IO) {
                    service.likeProduct(auth.currentUser!!.email.toString(), uuid)
                }
            } catch (e: Exception) {
                Log.d("ProductDetailsScreenViewModel", "likeProduct: ${e.message}")
            }
        }
    }

    fun unlikeProduct(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.IO) {
                    service.unlikeProduct(auth.currentUser!!.email.toString(), uuid)
                }
            } catch (e: Exception) {
                Log.d("ProductDetailsScreenViewModel", "unlikeProduct: ${e.message}")
            }
        }
    }

    fun updateUserReviews() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (profile == null) {
                    profile = sharedViewModel.getLocalProfile().value
                }
                if (profile != null) {
                    profile!!.reviews = profile!!.reviews?.plus(1)
                    db.collection("profiles").document(auth.currentUser!!.email.toString()).set(profile!!)
                }

                Log.d("UpdateUserReview", profile.toString())
                Log.d("ProductDetailsScreenViewModel", "updateUserReviews: Reviews updated")

            } catch (e: Exception) {
                Log.d("ProductDetailsScreenViewModel", "updateUserReviews: ${e.message}")
            }
        }
    }

//    private fun updateUserList(listUuid: String, newProduct: String, context: Context) {
//        if (profile == null) {
//            profile = com.dam.wewiza_front.screens.sharedViewModel.getLocalProfile().value
//        }
//        profile?.let { userProfile ->
//            viewModelScope.launch(Dispatchers.IO) {
//                try {
//                    // Encontrar la lista de compras correspondiente en el perfil del usuario
//                    val shoppingList = userProfile.shoppingListsList?.find { it.uuid == listUuid }
//                    if (shoppingList != null) {
//                        // Actualizar los productos de la lista de compras
//                        shoppingList.products.add(newProduct) // Agregar los UUID de los nuevos productos
//
//                        //userProfile.email?.let { updateProfileInFirebaseByEmail(it, userProfile) }
//
//                        Toast.makeText(context, "Producto añadido a la lista ${shoppingList.name}", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Log.d("updateUserList", "Shopping list with UUID $listUuid not found in user profile")
//                    }
//                } catch (e: Exception) {
//                    Log.d("updateUserList", "updateUserList: ${e.message}")
//                }
//            }
//        } ?: Log.d("updateUserList", "User profile not found")
//    }
//
//
//
//    private suspend fun updateProfileInFirebaseByEmail(userEmail: String, userProfile: Profile) {
//        try {
//            val querySnapshot = profilesCollection.whereEqualTo("email", userEmail).get().await()
//            if (!querySnapshot.isEmpty) {
//                // Se espera que haya solo un documento con el correo electrónico único
//                val profileDocument = querySnapshot.documents[0]
//                profileDocument.reference.set(userProfile).await()
//                Log.d("updateUserList", "Profile updated in Firebase for email: $userEmail")
//            } else {
//                Log.d("updateUserList", "User profile not found in Firebase for email: $userEmail")
//            }
//        } catch (e: Exception) {
//            Log.d("updateUserList", "Failed to update profile in Firebase: ${e.message}")
//        }
//    }
//
//    fun addProductToList(shoppingListUuid: String, productUuid: String, context: Context) {
//        val list = sharedViewModel.getLocalShoppingLists().value
//        val wantedList = list?.find { it.uuid == shoppingListUuid }
//        wantedList!!.products.add(productUuid)
//        updateUserList(shoppingListUuid, productUuid, context)
//    }


}