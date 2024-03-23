package com.dam.wewiza_front.models

data class Category(
    val id: String,
    val name: String,
    val products: List<Product>
)
