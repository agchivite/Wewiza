package dev.sbytmacke.firstcompose.screens.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.sbytmacke.firstcompose.Header
import dev.sbytmacke.firstcompose.data.repositories.ProductRepository
import dev.sbytmacke.firstcompose.models.Product
import dev.sbytmacke.firstcompose.routes.AppRoute

@Composable
fun ProductListScreen(
    category: String,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val searchText = Header()
        Text(
            text = category,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        ProductListBody(listProducts = getFilteredProducts(searchText), navController)
    }
}

@Composable
private fun ProductListBody(
    listProducts: List<Product>,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
    ) {

        items(listProducts) { product ->
            ProductRow(product, navController)
        }
    }
}

@Composable
private fun ProductRow(product: Product, navController: NavHostController) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                navController.navigate("${AppRoute.PRODUCT_DETAILS}/${product.id}")
            }),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = product.name,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun getFilteredProducts(searchText: String): List<Product> {
    val repo = ProductRepository() // TODO: Use DI
    val products: List<Product> = repo.getProducts()

    return if (searchText.isBlank()) {
        products
    } else {
        products.filter { it.name.startsWith(searchText, ignoreCase = true) }
    }
}

