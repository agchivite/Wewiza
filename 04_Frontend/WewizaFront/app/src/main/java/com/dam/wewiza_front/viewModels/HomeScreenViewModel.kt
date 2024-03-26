package com.dam.wewiza_front.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Location
import com.dam.wewiza_front.models.Origin
import com.dam.wewiza_front.models.Personaje
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private val service = RetrofitServiceFactory.makeRetrofitService()
    suspend fun getAllCategories(): Categories {
        return service.getPJById()
    }

}