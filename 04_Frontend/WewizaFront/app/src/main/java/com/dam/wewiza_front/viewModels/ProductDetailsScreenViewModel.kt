package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.services.RetrofitServiceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailsScreenViewModel : ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()
    private val sharedViewModel = SharedViewModel.instance
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var profile: Profile? = null


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

}