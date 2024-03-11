import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sbytmacke.firstcompose.BottomNavigationBar
import dev.sbytmacke.firstcompose.navigation.Navigation
import dev.sbytmacke.firstcompose.routes.AppRoute
import dev.sbytmacke.firstcompose.routes.RouteShowBottomBar

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    initDestination: String = AppRoute.HOME
) {
    val showBottomBar =
        navController.currentBackStackEntryAsState().value?.destination?.route in RouteShowBottomBar.bottomBarRoutes.map { it }

    // Documentación: https://developer.android.com/jetpack/compose/navigation
    // Doc. random: https://code.luasoftware.com/tutorials/android/jetpack-compose-hide-bottomnavigation-when-navigate-to-new-screen
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    selectedDestination = navController.currentDestination?.route ?: initDestination,
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Navigation(navController, initDestination)
        }
    }
}
