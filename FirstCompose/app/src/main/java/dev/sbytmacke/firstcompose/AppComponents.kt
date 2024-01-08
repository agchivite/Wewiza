package dev.sbytmacke.firstcompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.sbytmacke.firstcompose.navigation.AppNavigationActions
import dev.sbytmacke.firstcompose.routes.DESTINATIONS_BOTTOM_NAV_BAR

@Composable
fun Header(): String {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Título y logos en un Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Asignamos tamaño equitativo con los otros elementos de la columna
            val sizeColumn = 1f

            Image(
                painterResource(R.drawable.ic_gema),
                contentDescription = null,
                modifier = Modifier.size(52.dp).padding(0.dp, 0.dp, 12.dp, 0.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Wewiza",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "We find our deal!",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        // Margen de espacio
        Spacer(modifier = Modifier.height(30.dp))

        return SearchBar()
    }
    return ""
}

@Composable
fun SearchBar(): String {

    var searchText by remember { mutableStateOf("") }

    TextField(
        value = searchText,
        onValueChange = {
            searchText = it
        },
        placeholder = { Text("Buscar...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        singleLine = true
    )

    return searchText
}

@Composable
fun BottomNavigationBar(
    selectedDestination: String,
    navController: NavHostController
) {

    // Crea una instancia de la clase AppNavigationActions para gestionar las acciones de navegación
    val navigateAction = remember(navController) {
        AppNavigationActions(navController)
    }

    // Atajo para llamar a la función navigateTo en AppNavigationActions
    val navigateTo = navigateAction::navigateTo

    // Compose que representa la barra de navegación inferior
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        // Mostramos solo los dos primeros DESTINATIONS (destinos de navegación) = HOME y ACCOUNT
        DESTINATIONS_BOTTOM_NAV_BAR.take(2).forEach { destination ->
            // Compose que representa un elemento de la barra de navegación
            NavigationBarItem(
                // Verifica si este destino está actualmente seleccionado
                selected = selectedDestination == destination.route,
                // Define la acción a realizar cuando se hace clic en este elemento
                onClick = { navigateTo(destination) },
                // Icono que representa este destino en la barra de navegación
                icon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(id = destination.iconTextId)
                    )
                }
            )
        }
    }
}
