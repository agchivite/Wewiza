package com.dam.wewiza_front.models

data class Profile(
    val id: String,
    val name: String,
    val imageUrl: String,
    val reviews: Int,
    val shoppingListsList: List<ShoppingList>,
    val recentSearches: List<String> //List of the uuid of the last 5 searches
    )