package com.dam.wewiza_front.services

import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.models.Markets
import com.dam.wewiza_front.models.Product
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface ApiService {

    //http://wewiza.ddns.net/get_categories/
    @GET("categories")
    suspend fun getAllCategories(): Categories

    @GET("products")
    suspend fun getAllMarketsProduct(): Markets

    @GET("products/{market_id}")
    suspend fun getProductsPerMarket(@Path("market_id") market_id: String): List<Product>

    @GET("products/{id}")
    suspend fun getProductsPerCategory(@Path("id") id: String): Markets
}



object RetrofitServiceFactory {

    private val okHttpClient = OkHttpClient.Builder()
        .hostnameVerifier{hostname, _ -> hostname == "wewiza.ddns.net"}
        .connectTimeout(1, TimeUnit.MINUTES) // Aumenta el tiempo de espera de conexión
        .readTimeout(30, TimeUnit.SECONDS) // Aumenta el tiempo de espera de lectura
        .writeTimeout(15, TimeUnit.SECONDS) // Aumenta el tiempo de espera de escritura
        .build()

    fun makeRetrofitService(): ApiService{
        return Retrofit.Builder()
            .baseUrl("https://wewiza.ddns.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(ApiService::class.java)
    }
}