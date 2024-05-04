package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.screens.sharedViewModel
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductScreenViewModel : ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()
    val allProductsList = mutableStateOf(mutableListOf<Product>())

    init {

        if (sharedViewModel.getProducts().isEmpty()) {
            getMercadonaProducts()
            getAhorramasProducts()
            //  getCarrefourProducts()
            sharedViewModel.setProducts(allProductsList.value)
        }else{
            allProductsList.value.addAll(sharedViewModel.getProducts())
        }
    }



    private fun getMercadonaProducts() {
        viewModelScope.launch {
            try {
                val products = withContext(Dispatchers.IO) {
                    service.getProductsPerMarket("mercadona")
                }
                withContext(Dispatchers.Main) {
                    allProductsList.value.addAll(products)
                }
                Log.d("HomeScreenViewModel", "getMercadonaProducts: ${products.size}")
            } catch (e: Exception) {
                Log.d("HomeScreenViewModel", "getMercadonaProducts: ${e.message}")
            }
        }
    }

    private fun getAhorramasProducts() {
        viewModelScope.launch {
            try {
                val products = withContext(Dispatchers.IO) {
                    service.getProductsPerMarket("ahorramas")
                }
                withContext(Dispatchers.Main) {
                    allProductsList.value.addAll(products)
                }
                Log.d("HomeScreenViewModel", "getAhorramasProducts: ${products.size}")
            } catch (e: Exception) {
                Log.d("HomeScreenViewModel", "getAhorramasProducts: ${e.message}")
            }
        }
    }

    private fun getCarrefourProducts() {
        viewModelScope.launch {
            try {
                val products = withContext(Dispatchers.IO) {
                    service.getProductsPerMarket("carrefour")
                }
                withContext(Dispatchers.Main) {
                    allProductsList.value.addAll(products)
                }
                Log.d("HomeScreenViewModel", "getCarrefourProducts: ${products.size}")
            } catch (e: Exception) {
                Log.d("HomeScreenViewModel", "getCarrefourProducts: ${e.message}")
            }
        }
    }



}