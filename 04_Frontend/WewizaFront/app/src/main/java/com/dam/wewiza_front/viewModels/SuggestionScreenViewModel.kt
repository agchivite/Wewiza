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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.withContext

@OptIn(DelicateCoroutinesApi::class)
class SuggestionScreenViewModel: ViewModel() {


    val sharedViewModel = SharedViewModel.instance
    private val service = RetrofitServiceFactory.makeRetrofitService()
    var suggestions = mutableStateOf(mutableMapOf<String, List<Product>>())


   private val ioDispatcher: CoroutineDispatcher = newFixedThreadPoolContext(3, "IOPool")

    fun getSuggestions(wantedMarkets: List<String>, products: List<String>) {
        val baseUrl = "https://wewiza.ddns.net/suggest/id/"
        viewModelScope.launch(ioDispatcher) {
            try {
                val resultMap = mutableMapOf<String, List<Product>>()

                for (uuid in products) {
                    val url = buildUrl(baseUrl, uuid, wantedMarkets)
                    val suggestedProducts = async {service.getSuggestions(url)}
                    resultMap[uuid] = suggestedProducts.await()
                }

                withContext(Dispatchers.Main) {
                    Log.d("Suggestions", "getSuggestions: $resultMap")
                    suggestions.value = resultMap.toMutableMap()
                }
            } catch (e: Exception) {
                Log.e("SuggestionScreenViewModel", "getSuggestions: ${e.message}")
            }
        }
    }


    private fun buildUrl(baseUrl: String, uuid: String, wantedMarkets: List<String>): String {
        val urlBuilder = StringBuilder(baseUrl)
        urlBuilder.append(uuid)
        for (market in wantedMarkets) {
            urlBuilder.append("?filter_markets=").append(market)
        }
        return urlBuilder.toString()
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

    fun deleteProductFromSuggestions(product: Product, uuid: String) {
        val updatedSuggestions = suggestions.value.toMutableMap()
        updatedSuggestions[uuid] = updatedSuggestions[uuid]?.filter { it.uuid != product.uuid } ?: emptyList()
        suggestions.value = updatedSuggestions
    }


}