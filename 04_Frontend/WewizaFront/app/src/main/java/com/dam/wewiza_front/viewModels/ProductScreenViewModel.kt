package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.launch

class ProductScreenViewModel: ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()
    val allProductsList = mutableListOf<Product>()

    init {
        getAllProducts()
    }

    private fun getAllProducts() {
        viewModelScope.launch {
            val products = service.getProductsPerMarket()
            allProductsList.addAll(products.ahorramas)
            allProductsList.addAll(products.carrefour)
            allProductsList.addAll(products.mercadona)
            Log.d("HomeScreenViewModel", "getAllProducts: $products")
        }
    }

}