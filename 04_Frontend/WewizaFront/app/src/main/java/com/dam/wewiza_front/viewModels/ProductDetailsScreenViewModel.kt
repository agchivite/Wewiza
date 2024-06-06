package com.dam.wewiza_front.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
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
    private val _historyDetails = MutableStateFlow<List<Product>>(emptyList())
    val historyDetails: StateFlow<List<Product>> = _historyDetails

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
                Log.d("ProductDetailsScreenViewModel", "unlikeProduct: $result")
            } catch (e: Exception) {
                Log.d("ProductDetailsScreenViewModel", "unlikeProduct: ${e.message}")
            }
        }
    }

    fun getProductHistoryDetails() {
        viewModelScope.launch {
            val details = sharedViewModel.getHistoryDetails()
            _historyDetails.value = details
            Log.d("ProductDetailsScreenViewModel", "getProductHistoryDetails: ${_historyDetails.value}")
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



    fun addProductToList(
        shoppingListUuid: String,
        productUuid: String,
        context: Context,
        name: String
    ) {
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
        Toast.makeText(context, "Producto añadido a $name", Toast.LENGTH_SHORT).show()
    }

    fun prepareChartData(productHistoryDetails: List<Product>): Pair<List<Entry>, Map<Float, String>> {
        val entries = mutableListOf<Entry>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val uniqueMonths = mutableMapOf<Float, String>()
        var index = 0

        productHistoryDetails.forEach { product ->
            val date = dateFormat.parse(product.date_created)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            val monthValue = index.toFloat()

            if (!uniqueMonths.containsValue("${calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())} $year")) {
                uniqueMonths[monthValue] = "${calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())} $year"
                index++
            }

            val entry = Entry(monthValue, product.price_by_standard_measure.toFloat())
            entries.add(entry)
        }

        return Pair(entries, uniqueMonths)
    }


}
