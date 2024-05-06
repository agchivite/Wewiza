package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.screens.sharedViewModel
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel: ViewModel() {

    private val currentProduct = mutableStateOf<Product?>(null)
    private val loadedCategories = mutableListOf<Category>()
    private val loadedProducts = mutableListOf<Product>()
    var selectedCategories: MutableState<MutableState<MutableMap<String, Boolean>>> = mutableStateOf(
        mutableStateOf(loadedCategories.associate { it.id to true }.toMutableMap()))

    private val productHistoryDetails = mutableListOf<Product>()

    private val service = RetrofitServiceFactory.makeRetrofitService()

    companion object {
        val instance: SharedViewModel by lazy { SharedViewModel() }
    }

    fun getCategories(): MutableList<Category> {
         return loadedCategories
    }

    fun getProducts(): MutableList<Product> {
         return loadedProducts
    }

    fun setCategories(categories: MutableList<Category>) {
        loadedCategories.clear()
        loadedCategories.addAll(categories)
    }

    fun setProducts(products: List<Product>) {
        loadedProducts.clear()
        loadedProducts.addAll(products)
    }

    fun initializeSelectedCategories() {
        selectedCategories.value.value = loadedCategories.associate { it.id to true }.toMutableMap()
    }

    fun resetSelectedCategories() {
        selectedCategories.value.value = loadedCategories.associate { it.id to false }.toMutableMap()
    }

    fun getCurrentProduct(): Product {
        return currentProduct.value!!
    }

    fun setCurrentProduct(product: Product) {
        currentProduct.value = product
    }

    fun clearCurrentProduct() {
        currentProduct.value = null
    }

    fun setProductHistoryDetails() {
        if (currentProduct.value != null) {
            getProductHistoryDetails(currentProduct.value!!.uuid, currentProduct.value!!.store_name)
        }
    }

    fun getHistoryDetails(): MutableList<Product> {
       return productHistoryDetails
    }


    private fun getProductHistoryDetails(product_id: String, market_id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val products = withContext(Dispatchers.IO) {
                    service.getProductHistoryDetails(product_id, market_id)
                }
                withContext(Dispatchers.Main) {
                    productHistoryDetails.addAll(products)
                }
            } catch (e: Exception) {
                Log.d("ProductDetailsScreenViewModel", "getProductHistoryDetails: ${e.message}")
            }
        }
    }
}