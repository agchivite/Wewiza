package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private val service = RetrofitServiceFactory.makeRetrofitService()

    var allCategoriesList = mutableListOf<Category>()
    val allProductsList = mutableListOf<Product>()

    init {
        getAllCategories()
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

    private fun getAllCategories() {

        viewModelScope.launch {
            val categories = service.getAllCategories()
            allCategoriesList.addAll(categories.categories)
            Log.d("CategoriesScreenViewModel", "getAllCategories: ${categories.categories}")
        }

    }

    fun navigateToCategories(navController: NavController) {
        navController.navigate(AppScreens.CategoriesScreen.route)
    }

    fun navigateToProducts(navController: NavController) {
        navController.navigate(AppScreens.ProductScreen.route)
    }

}