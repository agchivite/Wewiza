package com.dam.wewiza_front.models

data class ShoppingList(
    var uuid: String = "",
    var products: MutableList<String> = mutableListOf(),  //Stores the uuid of the products, to be able to retrieve them from the database with the API call
    var name: String = ""
)