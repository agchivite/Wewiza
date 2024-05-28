package com.dam.wewiza_front.models

import androidx.compose.runtime.MutableState

data class Profile(
    var email: String? = null,
    var name: String? = null,
    var imageUrl: String? = null,
    var reviews: Int? = null,
    var shoppingListsList: List<ShoppingList>? = null,
    var recentSearches: List<String>? = null //List of the uuid of the last 5 searches
)
