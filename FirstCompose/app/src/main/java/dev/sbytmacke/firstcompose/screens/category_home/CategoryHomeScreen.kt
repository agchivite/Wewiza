package dev.sbytmacke.firstcompose.screens.category_home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.sbytmacke.firstcompose.Header
import dev.sbytmacke.firstcompose.R
import dev.sbytmacke.firstcompose.models.Category
import dev.sbytmacke.firstcompose.routes.AppRoute

@Composable
fun CategoryHomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.Center
    ) {
        val searchText = Header()
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            GridCategory(listCategories = getFilteredCategories(searchText), navController)
            FloatingActionButton(
                onClick = {
                    // Acciones
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    }
}

@Composable
private fun GridCategory(listCategories: List<Category>, navController: NavHostController) {
    LazyVerticalGrid(
        GridCells.Fixed(3)
    ) {
        items(listCategories.size) { item ->
            ButtonCategory(listCategories[item], navController)
        }
    }
}

@Composable
private fun ButtonCategory(category: Category, navController: NavHostController) {
    Column(
        modifier = Modifier
            .clickable(onClick = {
                navController.navigate("${AppRoute.PRODUCT_LIST}/${category.name}")
            })
            .padding(10.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Gray)
            .padding(18.dp)
            .clip(MaterialTheme.shapes.medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = category.name,
            modifier = Modifier
                .wrapContentWidth()
                .padding(0.dp, 0.dp, 0.dp, 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Image(
            painterResource(category.icon),
            contentDescription = null,
            modifier = Modifier.size(42.dp)
        )
    }
}

@Composable
private fun getFilteredCategories(searchText: String): List<Category> {
    val listCategories = listOf(
        Category("Frutas", R.drawable.ic_fruta),
        Category("Verduras", R.drawable.ic_verduras),
        Category("Repostería", R.drawable.ic_panaderia),
        Category("Lácteos", R.drawable.ic_lacteos),
        Category("Carnes", R.drawable.ic_carnes),
        Category("Pescados", R.drawable.ic_mariscos),
        Category("Envasados", R.drawable.ic_envasados),
        Category("Bebidas", R.drawable.ic_bebidas),
        Category("Congelados", R.drawable.ic_congelados)
    )

    return if (searchText.isBlank()) {
        listCategories
    } else {
        listCategories.filter { it.name.startsWith(searchText, ignoreCase = true) }
    }
}
