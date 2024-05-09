package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel : ViewModel() {


    /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!AFTER THIS, THERE ARE THE UTILS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */

    private val service = RetrofitServiceFactory.makeRetrofitService()

    companion object {
        val instance: SharedViewModel by lazy { SharedViewModel() }
    }


    /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!AFTER THIS, THERE ARE ALL THE VARIABLES !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */


    private val currentProduct = mutableStateOf<Product?>(null)
    private val loadedCategories = mutableListOf<Category>()
    private val loadedProducts = mutableListOf<Product>()
    var selectedCategories: MutableState<MutableState<MutableMap<String, Boolean>>> =
        mutableStateOf(
            mutableStateOf(loadedCategories.associate { it.id to true }.toMutableMap())
        )
    private val productHistoryDetails = mutableListOf<Product>()
    private val loggedUserProfile = mutableStateOf<Profile?>(null)
    private val localShoppingList = mutableStateOf<List<ShoppingList>?>(null)
    private val selectedList = mutableStateOf<ShoppingList?>(null)


    /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!AFTER THIS, THERE ARE ALL THE FUNCTIONS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */


    fun setSelectedList(shoppingList: ShoppingList) {
        selectedList.value = shoppingList
    }

    fun getSelectedList(): MutableState<ShoppingList?> {
        return selectedList
    }

    fun setLocalShoppingList(shoppingList: List<ShoppingList>?) {
        localShoppingList.value = shoppingList
    }

    fun getLocalShoppingList(): MutableState<List<ShoppingList>?> {
        return localShoppingList
    }

    fun setLocalProfile(profile: Profile?) {
        if (profile != null) {
            this.loggedUserProfile.value = profile
        }
    }

    fun getLocalProfile(): MutableState<Profile?> {
        return loggedUserProfile
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
        selectedCategories.value.value =
            loadedCategories.associate { it.id to false }.toMutableMap()
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

    fun resetLocalData() {
        loggedUserProfile.value = null
        productHistoryDetails.clear()
        localShoppingList.value = null
    }
}