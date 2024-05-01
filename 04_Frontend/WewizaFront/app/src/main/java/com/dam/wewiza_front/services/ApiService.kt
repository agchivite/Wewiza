package com.dam.wewiza_front.services

import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.models.Markets
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    //http://wewiza.ddns.net/get_categories/
    @GET("categories")
    suspend fun getAllCategories(): Categories

    @GET("products")
    suspend fun getProductsPerMarket(): Markets

    @GET("products/{id}")
    suspend fun getProductsPerCategory(@Path("id") id: String): Markets
}


object RetrofitServiceFactory {

    private val okHttpClient = OkHttpClient.Builder()
        .hostnameVerifier{hostname, _ -> hostname == "wewiza.ddns.net"}
        .build()

    fun makeRetrofitService(): ApiService{
        return Retrofit.Builder()
            .baseUrl("https://wewiza.ddns.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(ApiService::class.java)
    }
}