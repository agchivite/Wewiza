package com.dam.wewiza_front.navigation

import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class AppScreens(val route: String){
    object WelcomeScreen: AppScreens("welcome_screen")
    object HomeScreen: AppScreens("home_screen")
    object LoginScreen: AppScreens("login_screen")
    object RegisterScreen: AppScreens("register_screen")
    object CategoriesScreen: AppScreens ("categories_screen")
    object ChooseListScreen: AppScreens ("choose_list_screen")
    object CustomerSupportScreen: AppScreens ("customer_support_screen")
    object ListScreen: AppScreens ("list_screen")
    object MyListScreen: AppScreens ("my_list_screen")
    object ProductListScreen: AppScreens ("product_list_screen")
    object ProductScreen: AppScreens("product/{id}") {
        val arguments = listOf(navArgument("id") { type = NavType.StringType })
    }
    object ProfileScreen: AppScreens ("profile_screen")
    object SettingsScreen: AppScreens ("settings_screen")
    object SuggestionScreen: AppScreens ("suggestion_screen")
    object AboutUsScreen: AppScreens ("about_us_screen")


}
