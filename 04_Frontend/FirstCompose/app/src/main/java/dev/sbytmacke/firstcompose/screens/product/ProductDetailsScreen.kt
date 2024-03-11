package dev.sbytmacke.firstcompose.screens.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.sbytmacke.firstcompose.data.repositories.ProductRepository
import dev.sbytmacke.firstcompose.models.Product

@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavHostController
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val repo = ProductRepository() // TODO: Inyectar de alguna forma, para no acoplar

        val product: Product = remember(productId) {
            repo.getProduct(productId.toInt())
        }
        SetProductDetails(product)
    }
}

@Composable
private fun SetProductDetails(
    product: Product
    //navigateUp: () -> Unit // Se utiliza para hacer un botón de volver atrás
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = product.name + " " + product.id.toString(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

