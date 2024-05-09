package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ListScreenViewModel
import kotlinx.coroutines.delay
import okhttp3.internal.wait

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(listScreenViewModel: ListScreenViewModel, navController: NavHostController) {
    MyLightTheme {
         Scaffold(
             topBar = {
                 Constants.TopBarWithLogo(navController)
             }
         ) {

             ListScreenBodyContent(viewModel = listScreenViewModel, navController = navController)
         }
    }

}

@Composable
fun ListScreenBodyContent(viewModel: ListScreenViewModel, navController: NavHostController) {
    val productsList = viewModel.productsList.value
    val productsLoaded = productsList.isNotEmpty()

    if (!productsLoaded) {
        // Muestra el indicador de progreso mientras los productos se están cargando
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Muestra la lista de productos cuando están cargados
        LaunchedEffect(productsLoaded) {
            // Forzar una recomposición cuando los productos se hayan cargado
            delay(100) // Agregar un pequeño retraso para asegurar que la interfaz de usuario se actualice
        }

        Column(modifier = Modifier.padding(top = 70.dp)) {
            LazyColumn {
                items(productsList.size) { index ->
                    ListProductItem(product = productsList[index])
                }
            }
        }
    }
}


@Composable
fun ListProductItem(product: Product) {
    Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberImagePainter(data = product.image_url),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RectangleShape)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = product.name)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Price: ${product.price} €")
                Text(text = "Store: ${product.store_name}")
            }
        }
    }
}