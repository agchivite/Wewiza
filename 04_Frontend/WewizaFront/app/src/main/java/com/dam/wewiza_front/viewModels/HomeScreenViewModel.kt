package com.dam.wewiza_front.viewModels

import androidx.lifecycle.ViewModel
import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.services.RetrofitServiceFactory

class HomeScreenViewModel : ViewModel() {
    private val service = RetrofitServiceFactory.makeRetrofitService()
    suspend fun getAllCategories(): Categories {
        return service.getPJById()
    }

}