package com.dam.wewiza_front.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.services.RetrofitServiceFactory
import com.github.mikephil.charting.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProductDetailsScreenViewModel : ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()
    private val sharedViewModel = SharedViewModel.instance
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var profile: Profile? = null
    private val profilesCollection = db.collection("profiles")

    fun likeProductWithCallback(productId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val serviceResult = withContext(Dispatchers.IO) {
                    service.likeProduct(auth.currentUser!!.email.toString(), productId)
                }
                val result = serviceResult.values.firstOrNull() ?: false
                callback(result)
                Log.d("ProductDetailsScreenViewModel", "likeProduct: ${result}")
            } catch (e: Exception) {
                Log.d("ProductDetailsScreenViewModel", "likeProduct: ${e.message}")
            }
        }
    }

    fun unlikeProductWithCallback(productId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val serviceResult = withContext(Dispatchers.IO) {
                    service.unlikeProduct(auth.currentUser!!.email.toString(), productId)
                }
                val result = serviceResult.values.firstOrNull() ?: false
                callback(result)
                Log.d("ProductDetailsScreenViewModel", "unlikeProduct: ${result}")
            } catch (e: Exception) {
                Log.d("ProductDetailsScreenViewModel", "unlikeProduct: ${e.message}")
            }
        }
    }

    fun getProductHistoryDetails(): MutableList<Product> {
        return sharedViewModel.getHistoryDetails()
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

    fun recoverProfileData(user: FirebaseUser): Flow<List<ShoppingList>> = flow {
        val profile = MutableStateFlow<Profile?>(null)

        db.collection("profiles").document(user.email.toString()).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val loadedProfile = document.toObject(Profile::class.java)
                    profile.value = loadedProfile
                    sharedViewModel.setLocalProfile(loadedProfile)
                    Log.d("ProductDetailsScreenViewModel", profile.toString())
                } else {
                    Log.d("Profile", "Profile data not found")
                }
            }

        // Espera hasta que el valor de profile no sea nulo
        profile.collect {
            if (it != null) {
                // Una vez que se ha recuperado el perfil, emite la lista de listas de compras
                emit(it.shoppingListsList ?: emptyList())
            }
        }
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

    fun addProductToList(shoppingListUuid: String, productUuid: String, context: Context) {
        db.collection("profiles").document(auth.currentUser!!.email.toString()).get().addOnSuccessListener { document ->
            val profile = document.toObject(Profile::class.java)
            if (profile != null) {
                val shoppingList = profile.shoppingListsList?.find { it.uuid == shoppingListUuid }
                if (shoppingList != null) {
                    shoppingList.products.add(productUuid)
                    db.collection("profiles").document(auth.currentUser!!.email.toString()).set(profile)
                }
            }
        }
        Toast.makeText(context, "Producto añadido a la lista", Toast.LENGTH_SHORT).show()
    }

    fun prepareChartData(productHistoryDetails: List<Product>): List<Entry> {
        val entries = mutableListOf<Entry>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        productHistoryDetails.forEach { product ->
            val date = dateFormat.parse(product.date_created)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val monthYear = "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"

            // Convierte la fecha a un valor numérico que pueda ser usado en la gráfica
            val monthValue =
                (calendar.get(Calendar.MONTH) + 1 + (calendar.get(Calendar.YEAR) - 2024) * 12).toFloat()

            val entry = Entry(monthValue, product.price.toFloat())
            entries.add(entry)
        }

        return entries
    }

}
