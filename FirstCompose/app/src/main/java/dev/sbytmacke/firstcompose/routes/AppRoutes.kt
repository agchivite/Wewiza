package dev.sbytmacke.firstcompose.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import dev.sbytmacke.firstcompose.R

object AppRoute {
    const val HOME = "home"
    const val ACCOUNT = "account"
    const val SETTINGS = "settings"
    const val PRODUCT_LIST = "productList"
    const val PRODUCT_DETAILS = "productDetails"
    const val LOGIN = "login"
    const val REGISTER = "register"
}

object RouteShowBottomBar {
    val bottomBarRoutes = listOf(
        AppRoute.HOME,
        AppRoute.ACCOUNT,
        AppRoute.SETTINGS
    )
}

// Exclusivo para la barra de navegación inferior
data class DestinationBottomNavBar(
    val route: String,
    val selectedIcon: ImageVector,
    val iconTextId: Int
)

val DESTINATIONS_BOTTOM_NAV_BAR = listOf(
    DestinationBottomNavBar(
        route = AppRoute.HOME,
        selectedIcon = Icons.Default.Home,
        iconTextId = R.string.home
    ),
    DestinationBottomNavBar(
        route = AppRoute.ACCOUNT,
        selectedIcon = Icons.Default.AccountCircle,
        iconTextId = R.string.account
    )
)
