package com.dam.wewiza_front.navigation

sealed class AppScreens(val route: String){
    object WelcomeScreen: AppScreens("welcome_screen")
    object HomeScreen: AppScreens("home_screen")

}
