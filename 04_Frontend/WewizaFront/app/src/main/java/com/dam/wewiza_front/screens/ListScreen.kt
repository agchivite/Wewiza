package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ListScreenViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(listScreenViewModel: ListScreenViewModel, navController: NavHostController) {
    val selectedProductsIds = sharedViewModel.selectedList.value
    var productsList by remember { mutableStateOf(emptyList<Product>()) }
    Log.d("ListScreen", "Selected Products: ${selectedProductsIds!!.products.size}")
    var loading by remember { mutableStateOf(false) }

    if (selectedProductsIds.products.isNotEmpty()) {
        LaunchedEffect(selectedProductsIds) {
            loading = true
            val products = listScreenViewModel.getProductsFromList(selectedProductsIds)
            productsList = products
            Log.d("ListScreen", "Products: $products")
            loading = false
        }
    }

    MyLightTheme {
        Scaffold(
            topBar = {
                Constants.TopBarWithLogo(navController)
            },
        ) {
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                ListScreenBodyContent(
                    listScreenViewModel,
                    navController,
                    productsList
                ) { updatedProductsList ->
                    productsList = updatedProductsList
                }
            }
        }
    }
}

@Composable
fun ListScreenBodyContent(
    viewModel: ListScreenViewModel,
    navController: NavHostController,
    productsList: List<Product>,
    onProductsListChanged: (List<Product>) -> Unit
) {

    val showMarketDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 70.dp)
            .background(Color.LightGray)
    ) {
        // Columna con la lista de productos
        LazyColumn(
            modifier = Modifier.weight(1f) // Ocupa todo el espacio restante
        ) {
            items(productsList.size) { index ->
                ListProductItem(
                    product = productsList[index],
                    viewModel,
                    navController
                ) { deletedProduct ->
                    val updatedProductsList = productsList.toMutableList().apply {
                        remove(deletedProduct)
                    }
                    onProductsListChanged(updatedProductsList)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //product list total price
                Text(
                    text = "Total: ${"%.2f".format(productsList.sumOf { it.price })} €",
                    style = TextStyle(fontSize = 20.sp),
                    color = Color.Black
                )

                Button(onClick = {
                    showMarketDialog.value = true
                }) {
                    Text(text = "Sugerencias")
                }
            }
        }
    }
    if (showMarketDialog.value) {
        MarketSelectionDialog(showMarketDialog) { selectedMarkets ->
            sharedViewModel.setWantedMarket(selectedMarkets)
            viewModel.navigateToSuggestionScreen(navController, sharedViewModel.selectedList.value!!.uuid)
        }
    }

}

@Composable
fun ListProductItem(
    product: Product,
    viewModel: ListScreenViewModel,
    navController: NavHostController,
    onDelete: (Product) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = {
                sharedViewModel.clearCurrentProduct()
                sharedViewModel.setCurrentProduct(product)
                sharedViewModel.setProductHistoryDetails {
                    viewModel.navigateToProductDetailsScreen(navController)
                }
            })
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(model = product.image_url),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RectangleShape)
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f) // This makes the column take available space
            ) {
                Text(text = product.name, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Precio: ${product.price} €")
                Text(text = "Precio por medida: ${product.price_by_standard_measure} €/${
                    if (product.measure.lowercase().contains("mg") ||
                        product.measure.lowercase().contains("g") ||
                        product.measure.lowercase().contains("kg")
                    ) {
                        "Kg"
                    } else if (product.measure.lowercase().contains("ml") ||
                        product.measure.lowercase().contains("cl") ||
                        product.measure.lowercase().contains("l")
                    ) {
                        "L"
                    } else {
                        "Ud"
                    }
                }", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(5.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                DeleteButton(viewModel, onDelete, product)

                Spacer(modifier = Modifier.height(8.dp))

                if (product.store_name.trim().lowercase() == "mercadona") {
                    Image(
                        painter = painterResource(id = R.drawable.mercadona_logo),
                        contentDescription = "mercadona",
                        modifier = Modifier
                            .size(40.dp)
                    )
                } else if (product.store_name.trim().lowercase() == "ahorramas") {
                    Image(
                        painter = painterResource(id = R.drawable.ahorramas),
                        contentDescription = "ahorramas",
                        modifier = Modifier
                            .size(40.dp)
                    )
                } else if (product.store_name.trim().lowercase() == "carrefour") {
                    Image(
                        painter = rememberAsyncImagePainter(model = product.store_image_url),
                        contentDescription = "carrefour",
                        modifier = Modifier
                            .size(40.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteButton(viewModel: ListScreenViewModel, onDelete: (Product) -> Unit, product: Product) {
    IconButton(onClick = {
        viewModel.deleteProduct(product)
        onDelete(product)
    }) {
        Icon(
            painter = painterResource(id = R.drawable.delete),
            contentDescription = "Delete"
        )
    }

}


