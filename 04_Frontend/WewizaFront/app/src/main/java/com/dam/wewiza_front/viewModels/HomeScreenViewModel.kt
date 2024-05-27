package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.TopProduct
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


    var topCategoriesList = mutableStateOf(mutableListOf<Category>())
    var topProductsList = mutableStateOf(mutableListOf<TopProduct>())

    var isTopProductsLoading = mutableStateOf(true)
    var isTopCategoriesLoading = mutableStateOf(true)

    val isConnectionError = mutableStateOf(false)

    init {
        checkApiConnection()
        getAllCategories()
        getTopCategories()
        getTopProducts()
        sharedViewModel.setProducts(allProductsList)

    }


    private fun checkApiConnection() {
        viewModelScope.launch {
            val result = safeApiCall { service.getAllMarketsProduct() }
            isConnectionError.value = result.isFailure
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getTopProducts() {
        viewModelScope.launch {
            isTopProductsLoading.value = true
            try {
                val products = withContext(Dispatchers.IO) {
                    service.getTopProducts()
                }
                withContext(Dispatchers.Main) {
                    topProductsList.value = products.toMutableList()
                }
            } catch (e: Exception) {
                Log.d("HomeScreenViewModel", "getTopProducts: ${e.message}")
            } finally {
                isTopProductsLoading.value = false
            }
        }
    }

    private fun getTopCategories() {
        viewModelScope.launch {
            isTopCategoriesLoading.value = true
            try {
                val categories = withContext(Dispatchers.IO) {
                    service.getTopCategories()
                }
                withContext(Dispatchers.Main) {
                    topCategoriesList.value = categories.toMutableList()
                }
            } catch (e: Exception) {
                Log.d("HomeScreenViewModel", "getTopCategories: ${e.message}")
            } finally {
                isTopCategoriesLoading.value = false
            }
        }
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

    fun navigateToProductsScreen(id: String, navController: NavController) {
        val route = AppScreens.ProductScreen.route
        sharedViewModel.resetSelectedCategories()
        sharedViewModel.selectedCategories.value.value[id] = true
        navController.navigate(route)
    }

    fun navigateToProductDetailsScreen(navController: NavController) {
        navController.navigate(AppScreens.ProductDetailsScreen.route)
    }

}