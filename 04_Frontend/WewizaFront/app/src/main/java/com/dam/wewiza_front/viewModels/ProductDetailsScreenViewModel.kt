package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.services.RetrofitServiceFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailsScreenViewModel : ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()
    private val sharedViewModel = SharedViewModel.instance
    private val auth = FirebaseAuth.getInstance()


    fun getProductHistoryDetails(): MutableList<Product> {
        if (sharedViewModel.getHistoryDetails().isNotEmpty()){
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

}