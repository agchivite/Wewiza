package com.dam.wewiza_front.models

data class Product(
    val uuid: String,
    val category_id: String,
    val image_url: String,
    val measure: String,
    val name: String,
    val price: Float,
    val price_by_standard_measure: Float,
    val quantity_measure: Int,
    val store_image_url: String,
    val store_name: String,

)