package com.dam.wewiza_front.viewModels

import androidx.lifecycle.ViewModel
import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.models.Category

class SharedViewModel: ViewModel() {

    val loadedCategories = mutableListOf<Category>()

    companion object {
        val instance: SharedViewModel by lazy { SharedViewModel() }
    }
}