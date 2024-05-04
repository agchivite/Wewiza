package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.screens.sharedViewModel
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeScreenViewModel : ViewModel() {
    private val service = RetrofitServiceFactory.makeRetrofitService()

    var allCategoriesList = mutableListOf<Category>()
    val allProductsList = mutableListOf<Product>()

    init {
        getAllCategories()
        getMercadonaProducts()
        getAhorramasProducts()
        sharedViewModel.setProducts(allProductsList)
      //  getCarrefourProducts()
    }


    private fun getMercadonaProducts() {
        viewModelScope.launch {
            try {
                val products = withContext(Dispatchers.IO) {
                    service.getProductsPerMarket("mercadona")
                }
                withContext(Dispatchers.Main) {
                    allProductsList.addAll(products)
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
                    allProductsList.addAll(products)
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
                    allProductsList.addAll(products)
                }
                Log.d("HomeScreenViewModel", "getCarrefourProducts: ${products.size}")
            } catch (e: Exception) {
                Log.d("HomeScreenViewModel", "getCarrefourProducts: ${e.message}")
            }
        }
    }



    private fun getAllCategories() {
        viewModelScope.launch {
            try{
                val categories = withContext(Dispatchers.IO) {
                    service.getAllCategories()
                }
                withContext(Dispatchers.Main) {
                    allCategoriesList.addAll(categories.categories)
                    Log.d("HomeScreenViewModel", "getAllCategories: ${categories.categories.size}")
                    sharedViewModel.setCategories(categories.categories as MutableList<Category>)
                    sharedViewModel.initializeSelectedCategories()
                }
            }catch (e: Exception) {
                Log.d("HomeScreenViewModel", "getAllCategories: ${e.message}")
            }
        }
    }

    fun navigateToCategories(navController: NavController) {
        navController.navigate(AppScreens.CategoriesScreen.route)
    }

    fun navigateToProducts(navController: NavController) {
        navController.navigate(AppScreens.ProductScreen.route)
    }

}