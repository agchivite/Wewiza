package com.dam.wewiza_front.models

data class ShoppingList(
    val uuid: String,

    //stores the uuid of the products that the user wants to buy, uses the uuid to make a GET request to the server
    val products: List<String>,
    val name: String,

    )