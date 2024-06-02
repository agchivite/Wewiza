@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.SuggestionScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SuggestionScreen(
    viewModel: SuggestionScreenViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {
            Constants.TopBarWithLogo(navController = navController)
        }
    ) {
        MyLightTheme {
            SuggestionScreenBodyContent(viewModel, navController)
        }
    }
}

@Composable
fun SuggestionScreenBodyContent(
    viewModel: SuggestionScreenViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 70.dp)
    ) {

        val selectedList = viewModel.sharedViewModel.selectedList.value
        val wantedMarket = viewModel.sharedViewModel.getWantedMarket()
        var productsList by remember { mutableStateOf(emptyList<Product>()) }
        val suggestions by viewModel.suggestions

        LaunchedEffect(selectedList!!.products.isNotEmpty()) {
            viewModel.getSuggestions(wantedMarket, selectedList.products)
            val products = viewModel.getProductsFromList(selectedList)
            productsList = products
            Log.d("ListScreen", "Products: $products")
        }

        if (suggestions.isEmpty() || productsList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {

            val combinedMap = productsList.associateWith { product ->
                suggestions[product.uuid] ?: emptyList()
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(combinedMap.entries.toList().size) { index ->
                    val entry = combinedMap.entries.toList()[index]
                    val product = entry.key
                    val suggestedProducts = entry.value
                    ProductItem(product, suggestedProducts)
                }
            }
        }


    }

}

@Composable
fun ProductItem(product: Product, suggestedProducts: List<Product>) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen y nombre del producto a la izquierda
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = rememberImagePainter(data = product.image_url),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(80.dp)
                )
                Text(
                    text = product.name,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Flecha en el medio
            Box(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(text = "→", fontSize = 34.sp)
            }

            // Lista horizontal de productos sugeridos a la derecha
            LazyRow(
                modifier = Modifier.weight(2f)
            ) {
                items(suggestedProducts.size) { index ->
                    SuggestedProductItem(suggestedProducts[index])
                }
            }
        }
    }
}

@Composable
fun SuggestedProductItem(product: Product) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(4.dp)
            .size(230.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = product.image_url),
                contentDescription = "Suggested Product Image",
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                text = product.name,
                modifier = Modifier.padding(top = 4.dp)
            )
            Row (modifier = Modifier.weight(1f)) {

                Icon(
                    painter = painterResource(id = R.drawable.baseline_cancel_24),
                    contentDescription = "cancel",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFFD32F2F)
                )

                Icon(
                    painter = painterResource(id = R.drawable.accept),
                    contentDescription = "accept",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xFF4CAF50)
                )

            }
        }
    }
}


