package com.dam.wewiza_front.viewModels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.launch

class CategoriesScreenViewModel : ViewModel() {

    private val service = RetrofitServiceFactory.makeRetrofitService()

    var allCategoriesList = mutableListOf<Category>()

    init {
        getAllCategories()
    }

    private fun getAllCategories() {

        viewModelScope.launch {
            val categories = service.getAllCategories()
            allCategoriesList.addAll(categories.categories)
            Log.d("CategoriesScreenViewModel", "getAllCategories: ${categories.categories}")
        }

    }

    fun navigateToProductsScreen(id: String, navController: NavController) {
        val route = AppScreens.ProductScreen.route.replace("{id}", id)
        navController.navigate(route)
    }


}