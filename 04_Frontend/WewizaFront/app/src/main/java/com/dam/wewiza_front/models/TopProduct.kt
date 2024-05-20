package com.dam.wewiza_front.models


data class TopProduct(
    val category_id: String,
    val date_created: String,
    val image_url: String,
    val measure: String,
    val name: String,
    val price: Double,
    val price_by_standard_measure: Double,
    val quantity_measure: Double,
    val store_image_url: String,
    val store_name: String,
    val url: String,
    val uuid: String,
    val num_likes: Int,
    val profit: Int,
    val profit_percentage: Int

)
