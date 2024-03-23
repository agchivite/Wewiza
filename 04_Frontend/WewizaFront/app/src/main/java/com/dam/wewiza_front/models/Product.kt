package com.dam.wewiza_front.models

import java.util.UUID

data class Product(
    val id: UUID,
    val categoryId: String,
    val name: String,
    val price: Float,
    val pricePerMeasure: Float,
    val description: String,
    val image: String,
    val url: String,
    val store: Store
)
