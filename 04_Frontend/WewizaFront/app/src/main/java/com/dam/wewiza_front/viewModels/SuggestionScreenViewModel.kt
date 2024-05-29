package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.services.RetrofitServiceFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class SuggestionScreenViewModel: ViewModel() {


    val sharedViewModel = SharedViewModel.instance
    private val service = RetrofitServiceFactory.makeRetrofitService()
    var suggestions = mutableStateOf(mutableMapOf<String, List<Product>>())


   private val ioDispatcher: CoroutineDispatcher = newFixedThreadPoolContext(4, "IOPool")

    fun getSuggestions(wantedMarkets: List<String>, products: List<String>) {
        val baseUrl = "https://wewiza.ddns.net/suggest/id/"
        val urlBuilder = StringBuilder(baseUrl)
        val urlList = mutableListOf<String>()

        // Append all UUIDs and markets to the URL
        products.forEachIndexed { index, uuid ->
            urlBuilder.append(uuid)
            for (market in wantedMarkets) {
                urlBuilder.append("?filter_markets=").append(market)
            }
            urlList.add(urlBuilder.toString())
            Log.d("SuggestionScreenViewModel", "getSuggestions: $urlList")
        }
        viewModelScope.launch(ioDispatcher) {
            try {
                val resultMap = mutableMapOf<String, List<Product>>()

                for (i in products.indices) {
                    for (url in urlList) {
                        val product = async { service.getSuggestions(url) }
                        val result = product.await()
                        resultMap[products[i]] = result
                    }
                }
                // Move the result to the main thread and update the suggestions LiveData
                withContext(Dispatchers.Main) {
                    suggestions.value = resultMap.toMutableMap()
                    Log.d("SuggestionScreenViewModel", "getSuggestions: ${suggestions.value}")
                }
            } catch (e: Exception) {
                Log.e("SuggestionScreenViewModel", "getSuggestions: ${e.message}")
            }
        }
    }


    suspend fun getProductsFromList(selectedProductsIds: ShoppingList?): List<Product> {
        var updatedProducts = listOf<Product>()
        if (selectedProductsIds != null && selectedProductsIds.products.isNotEmpty()) {
            updatedProducts = selectedProductsIds.products.map { service.getProductById(it) }
            Log.d("getProductsFromList", updatedProducts.toString())
            while (updatedProducts.size != selectedProductsIds.products.size) {
                delay(100)
            }
        }

        return updatedProducts
    }



}