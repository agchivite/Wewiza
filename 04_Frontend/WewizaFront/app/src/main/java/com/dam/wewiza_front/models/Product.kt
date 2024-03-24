package com.dam.wewiza_front.models

import java.util.UUID

data class Product(
    val uuid: String,
    val categoryUuid: String,
    val name: String,
    val price: Float,
    val pricePerMeasure: Float,
    val measure: String,
    val measureQuantity: String,
    val image: String,
    val url: String,
    val store: Store
)
