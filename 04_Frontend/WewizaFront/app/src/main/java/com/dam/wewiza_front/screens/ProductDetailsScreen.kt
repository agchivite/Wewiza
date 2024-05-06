package com.dam.wewiza_front.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ProductDetailsScreenViewModel
import com.dam.wewiza_front.viewModels.SharedViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    viewModel: ProductDetailsScreenViewModel,
    navController: NavController
) {


    Scaffold(topBar = {
        Constants.TopBarWithLogo(navController)
    }) {
        MyLightTheme {
            val sharedViewModel = SharedViewModel.instance
            ProductDetailsScreenBodyContent(viewModel, navController, sharedViewModel)
        }
    }
}

@Composable
fun ProductDetailsScreenBodyContent(
    viewModel: ProductDetailsScreenViewModel,
    navController: NavController,
    sharedViewModel: SharedViewModel
) {

    val currentProduct = sharedViewModel.getCurrentProduct()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        PhotoField(currentProduct)
        ProductDetailsFields(currentProduct,viewModel)
        GraphicField(viewModel)
    }

}


@Composable
fun GraphicField(viewModel: ProductDetailsScreenViewModel) {
    val productHistoryDetails = viewModel.getProductHistoryDetails().sortedBy { it.date_created }
    if (productHistoryDetails.isNotEmpty()) {
        val maxPrice = productHistoryDetails.maxOf { it.price }
        val minPrice = productHistoryDetails.minOf { it.price }
        val priceRange = maxPrice - minPrice

        val canvasHeight = 200f // Altura del Canvas en dp
        val entries = productHistoryDetails.mapIndexed { index, product ->
            val normalizedPrice = ((product.price - minPrice) / priceRange) * canvasHeight
            Offset(index.toFloat(), (canvasHeight - normalizedPrice).toFloat())
        }

        Canvas(modifier = Modifier.fillMaxWidth().background(Color.Blue).height(canvasHeight.dp)) {
            val path = Path().apply {
                moveTo(entries.first().x, entries.first().y)
                entries.drop(1).forEach { point ->
                    lineTo(point.x, point.y)
                }
            }

            drawPath(
                path = path,
                color = Color.Blue,
                style = Stroke(width = 4f)
            )
        }
    } else {
        Text(text = "No hay datos para mostrar")
    }
}
@Composable
fun ProductDetailsFields(currentProduct: Product, viewModel: ProductDetailsScreenViewModel) {
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
    ) {

        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {

            Column (modifier = Modifier.padding(20.dp)){
                Text(text = "Nombre: ${currentProduct.name}")
                Text(text = "Tienda: ${currentProduct.store_name}")
                Text(text = "Precio: ${currentProduct.price} €")
            }

            Row(modifier = Modifier.padding(20.dp)) {
                Button(onClick = { viewModel.unlikeProduct(currentProduct.uuid) }) {
                    Text(text = "-")
                }
                Text(text = currentProduct.num_likes.toString())

                Button(onClick = { viewModel.likeProduct(currentProduct.uuid) }) {
                    Text(text = "+")
                }
            }

        }
    }
}

@Composable
fun PhotoField(product: Product) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(product.image_url)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.product_placeholder),
        contentDescription = "Product image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .padding(top = 20.dp)
            .width(350.dp)
            .height(250.dp)
            .clip(RectangleShape)
            .border(2.dp, Color.Black, RectangleShape)

    )
}
