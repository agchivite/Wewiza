package com.dam.wewiza_front.viewModels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.dam.wewiza_front.navigation.AppScreens

class ProfileScreenViewModel(): ViewModel() {
    fun navigateToMyListScreen(navController: NavController) {
        navController.navigate(AppScreens.MyListScreen.route)
    }

    fun navigateToCustomerSupportScreen(navController: NavController) {
        navController.navigate(AppScreens.CustomerSupportScreen.route)
    }
}