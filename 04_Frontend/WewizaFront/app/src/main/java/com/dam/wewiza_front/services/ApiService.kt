package com.dam.wewiza_front.services

import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Personaje
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    //http://wewiza.ddns.net/get_categories/
    @GET("get_categories")
    suspend fun getPJById(): Categories

}


object RetrofitServiceFactory {
    fun makeRetrofitService(): ApiService{
        return Retrofit.Builder()
            .baseUrl("http://wewiza.ddns.net:80/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }
}