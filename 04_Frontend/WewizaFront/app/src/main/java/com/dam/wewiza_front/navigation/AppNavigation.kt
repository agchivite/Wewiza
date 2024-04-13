package com.dam.wewiza_front.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dam.wewiza_front.MainActivity
import com.dam.wewiza_front.screens.AboutUsScreen
import com.dam.wewiza_front.screens.CategoriesScreen
import com.dam.wewiza_front.screens.CustomerSupportScreen
import com.dam.wewiza_front.screens.HomeScreen
import com.dam.wewiza_front.screens.LoginScreen
import com.dam.wewiza_front.screens.MyListsScreen
import com.dam.wewiza_front.screens.ProfileScreen
import com.dam.wewiza_front.screens.RegisterScreen
import com.dam.wewiza_front.screens.SettingsScreen
import com.dam.wewiza_front.screens.SuggestionScreen
import com.dam.wewiza_front.screens.WelcomeScreen
import com.dam.wewiza_front.viewModels.AboutUsScreenViewModel
import com.dam.wewiza_front.viewModels.CategoriesScreenViewModel
import com.dam.wewiza_front.viewModels.CustomerSupportScreenViewModel
import com.dam.wewiza_front.viewModels.HomeScreenViewModel
import com.dam.wewiza_front.viewModels.LoginScreenViewModel
import com.dam.wewiza_front.viewModels.MyListsScreenViewModel
import com.dam.wewiza_front.viewModels.ProfileScreenViewModel
import com.dam.wewiza_front.viewModels.RegisterScreenViewModel
import com.dam.wewiza_front.viewModels.SettingsScrennViewModel
import com.dam.wewiza_front.viewModels.SuggestionScreenViewModel
import com.dam.wewiza_front.viewModels.WelcomeScreenViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AppNavigation(
    welcomeScreenViewModel: WelcomeScreenViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    loginScreenViewModel: LoginScreenViewModel,
    registerScreenViewModel: RegisterScreenViewModel,
    aboutUsScreenViewModel: AboutUsScreenViewModel,
    suggestionScreenViewModel: SuggestionScreenViewModel,
    categoriesScreenViewModel: CategoriesScreenViewModel,
    profileScreenViewModel: ProfileScreenViewModel?,
    settingsScreenViewModel: SettingsScrennViewModel,
    myListsScreenViewModel: MyListsScreenViewModel,
    customerSupportScreenViewModel: CustomerSupportScreenViewModel,
    mainActivity: MainActivity
) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    var startDestination = AppScreens.HomeScreen.route

    if (auth.currentUser ==null){
        startDestination = AppScreens.WelcomeScreen.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = AppScreens.WelcomeScreen.route) {
            WelcomeScreen(welcomeScreenViewModel, navController)
        }
        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(homeScreenViewModel, navController)
        }
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(loginScreenViewModel, navController)
        }
        composable(route = AppScreens.RegisterScreen.route) {
            RegisterScreen(registerScreenViewModel, navController)
        }
        composable(route = AppScreens.AboutUsScreen.route) {
            AboutUsScreen(aboutUsScreenViewModel, navController)
        }
        composable(route = AppScreens.SuggestionScreen.route) {
            SuggestionScreen(suggestionScreenViewModel, navController)
        }
        composable(route = AppScreens.CategoriesScreen.route) {
            CategoriesScreen(categoriesScreenViewModel, navController)
        }
        composable(route = AppScreens.ProfileScreen.route) {
            if (profileScreenViewModel != null) {
                ProfileScreen(profileScreenViewModel, navController)
            } else {
                WelcomeScreen(welcomeScreenViewModel, navController)
            }
        }
        composable(route = AppScreens.SettingsScreen.route){
            SettingsScreen(settingsScreenViewModel, navController, mainActivity)
        }
        composable(route = AppScreens.MyListScreen.route){
            MyListsScreen(myListsScreenViewModel, navController)
        }
        composable(route = AppScreens.CustomerSupportScreen.route){
            CustomerSupportScreen(customerSupportScreenViewModel, navController)
        }
    }
}