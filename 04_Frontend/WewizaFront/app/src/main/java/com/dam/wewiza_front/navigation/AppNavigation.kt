package com.dam.wewiza_front.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dam.wewiza_front.screens.HomeScreen
import com.dam.wewiza_front.screens.WelcomeScreen
import com.dam.wewiza_front.viewModels.WelcomeScreenViewModel


@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.WelcomeScreen.route){
        composable(route = AppScreens.WelcomeScreen.route){
            WelcomeScreen(viewModel = WelcomeScreenViewModel(), navController)
        }
        composable(route = AppScreens.HomeScreen.route){
            HomeScreen(navController)
        }
    }
}