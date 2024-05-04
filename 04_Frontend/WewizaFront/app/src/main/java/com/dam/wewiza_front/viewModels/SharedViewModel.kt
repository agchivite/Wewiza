package com.dam.wewiza_front.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.screens.sharedViewModel

class SharedViewModel: ViewModel() {

    private val loadedCategories = mutableListOf<Category>()
    private val loadedProducts = mutableListOf<Product>()
    var selectedCategories: MutableState<MutableState<MutableMap<String, Boolean>>> = mutableStateOf(
        mutableStateOf(loadedCategories.associate { it.id to true }.toMutableMap()))

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
}