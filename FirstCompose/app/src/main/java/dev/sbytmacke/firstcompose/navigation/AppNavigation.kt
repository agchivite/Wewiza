package dev.sbytmacke.firstcompose.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.sbytmacke.firstcompose.routes.AppRoute
import dev.sbytmacke.firstcompose.routes.DestinationBottomNavBar
import dev.sbytmacke.firstcompose.screens.account.AccountScreen
import dev.sbytmacke.firstcompose.screens.category_home.CategoryHomeScreen
import dev.sbytmacke.firstcompose.screens.login.LoginScreen
import dev.sbytmacke.firstcompose.screens.product.ProductDetailScreen
import dev.sbytmacke.firstcompose.screens.products.ProductListScreen
import dev.sbytmacke.firstcompose.screens.register.RegisterScreen
import dev.sbytmacke.firstcompose.viewmodels.LoginScreenViewModel

@Composable
fun Navigation(navController: NavHostController, initDestination: String) {

    val loginUserViewModel = LoginScreenViewModel()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavHost(navController = navController, startDestination = initDestination) {
            composable(AppRoute.HOME) {
                CategoryHomeScreen(navController)
            }
            composable(AppRoute.ACCOUNT) {
                AccountScreen(navController, initDestination)
            }
            composable(route = "${AppRoute.PRODUCT_LIST}/{categoryName}") { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "404: Category Not Found"
                ProductListScreen(categoryName, navController)
            }
            composable(route = "${AppRoute.PRODUCT_DETAILS}/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: "404: Product Not Found"
                ProductDetailScreen(productId, navController)
            }
            composable(route = AppRoute.LOGIN) {
                LoginScreen(navController, loginUserViewModel)
            }
            composable(route = AppRoute.REGISTER) {
                RegisterScreen(navController, loginUserViewModel)
            }
        }
    }
}

class AppNavigationActions(private val navController: NavHostController) {
    // Función para navegar a un destino específico
    fun navigateTo(destination: DestinationBottomNavBar) {
        // Utiliza el NavHostController para realizar la navegación al destino especificado
        navController.navigate(destination.route) {
            // Define el comportamiento de la pila de navegación al realizar la navegación
            popUpTo(navController.graph.findStartDestination().id) {
                // Indica que se debe guardar el estado de los destinos eliminados
                saveState = true
            }
            // Indica que si el destino ya está en la parte superior de la pila, se reiniciará su estado
            launchSingleTop = true
        }
    }
}
