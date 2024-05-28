package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.screens.sharedViewModel
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductScreenViewModel : ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()

    private val _allProductsList = MutableStateFlow<List<Product>>(emptyList())
    val allProductsList: StateFlow<List<Product>> get() = _allProductsList

    private val _isProductsLoading = MutableStateFlow(true)
    val isProductsLoading: StateFlow<Boolean> get() = _isProductsLoading




    init {
        loadProducts()
    }


    private fun loadProducts() {
        viewModelScope.launch {
            if (sharedViewModel.getProducts().isEmpty()) {
                _isProductsLoading.value = true
                val products = mutableListOf<Product>()
                products.addAll(getMercadonaProducts())
                products.addAll(getAhorramasProducts())
                products.addAll(getCarrefourProducts())
                _allProductsList.value = products
                sharedViewModel.setProducts(products)
                _isProductsLoading.value = false
            } else {
                _allProductsList.value = sharedViewModel.getProducts()
            }
        }
    }


    private suspend fun getMercadonaProducts(): List<Product> {
        return try {
            service.getProductsPerMarket("mercadona")
        } catch (e: Exception) {
            Log.d("HomeScreenViewModel", "getMercadonaProducts: ${e.message}")
            emptyList()
        }
    }

    private suspend fun getAhorramasProducts(): List<Product> {
        return try {
            service.getProductsPerMarket("ahorramas")
        } catch (e: Exception) {
            Log.d("HomeScreenViewModel", "getAhorramasProducts: ${e.message}")
            emptyList()
        }
    }

    private suspend fun getCarrefourProducts(): List<Product> {
        return try {
            service.getProductsPerMarket("carrefour")
        } catch (e: Exception) {
            Log.d("HomeScreenViewModel", "getCarrefourProducts: ${e.message}")
            emptyList()
        }
    }



//
//
//    private fun getMercadonaProducts() {
//        viewModelScope.launch {
//            try {
//                val products = withContext(Dispatchers.IO) {
//                    service.getProductsPerMarket("mercadona")
//                }
//                withContext(Dispatchers.Main) {
//                    allProductsList.value.addAll(products)
//                }
//                Log.d("HomeScreenViewModel", "getMercadonaProducts: ${products.size}")
//            } catch (e: Exception) {
//                Log.d("HomeScreenViewModel", "getMercadonaProducts: ${e.message}")
//            }
//        }
//    }
//
//    private fun getAhorramasProducts() {
//        viewModelScope.launch {
//            try {
//                val products = withContext(Dispatchers.IO) {
//                    service.getProductsPerMarket("ahorramas")
//                }
//                withContext(Dispatchers.Main) {
//                    allProductsList.value.addAll(products)
//                }
//                Log.d("HomeScreenViewModel", "getAhorramasProducts: ${products.size}")
//            } catch (e: Exception) {
//                Log.d("HomeScreenViewModel", "getAhorramasProducts: ${e.message}")
//            }
//        }
//    }

//    private fun getCarrefourProducts() {
//        viewModelScope.launch {
//            try {
//                val products = withContext(Dispatchers.IO) {
//                    service.getProductsPerMarket("carrefour")
//                }
//                withContext(Dispatchers.Main) {
//                    allProductsList.value.addAll(products)
//                }
//                Log.d("HomeScreenViewModel", "getCarrefourProducts: ${products.size}")
//            } catch (e: Exception) {
//                Log.d("HomeScreenViewModel", "getCarrefourProducts: ${e.message}")
//            }
//        }
//    }

    fun navigateToProductDetailsScreen(navController: NavHostController) {
        navController.navigate(AppScreens.ProductDetailsScreen.route)
    }


}