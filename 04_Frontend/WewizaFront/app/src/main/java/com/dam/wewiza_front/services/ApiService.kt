package com.dam.wewiza_front.services

import com.dam.wewiza_front.models.Categories
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.models.Markets
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.TopProduct
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

    @GET("products/market/{market_id}")
    suspend fun getProductsPerMarket(@Path("market_id") market_id: String): List<Product>

    @GET("products/category/id/{id}")
    suspend fun getProductsPerCategory(@Path("id") id: String): Markets

    @GET("like/{product_id}/email/{user_email}")
    suspend fun likeProduct(
        @Path("user_email") user_email: String,
        @Path("product_id") product_id: String
    ):Map<String, Boolean>

    @GET("unlike/{product_id}/email/{user_email}")
    suspend fun unlikeProduct(
        @Path("user_email") user_email: String,
        @Path("product_id") product_id: String
    ):  Map<String, Boolean>

    @GET("product/details/id/{product_id}")
    suspend fun getProductHistoryDetails(@Path("product_id") product_id: String): List<Product>

    @GET("product/id/{product_id}")
    suspend fun getProductById(@Path("product_id") product_id: String): Product

    @GET("products/market/{market_id}/range/{init}/{end}")
    suspend fun getProductsPerMarketInRange(
        @Path("market_id") market_id: String,
        @Path("init") init: Int,
        @Path("end") end: Int
    ): List<Product>

    @GET("products/top")
    suspend fun getTopProducts(): List<TopProduct>

    @GET("categories/top")
    suspend fun getTopCategories(): List<Category>

}


object RetrofitServiceFactory {

    private val interceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .hostnameVerifier { hostname, _ -> hostname == "wewiza.ddns.net" }
        .connectTimeout(200, TimeUnit.MILLISECONDS) // Aumenta el tiempo de espera de conexión
        .readTimeout(30, TimeUnit.SECONDS) // Aumenta el tiempo de espera de lectura
        .writeTimeout(15, TimeUnit.SECONDS) // Aumenta el tiempo de espera de escritura
        .addInterceptor(interceptor)
        .build()

    fun makeRetrofitService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://wewiza.ddns.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(ApiService::class.java)
    }
}