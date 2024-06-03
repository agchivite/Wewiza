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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
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

    if (selectedProductsIds!!.products.isNotEmpty()) {
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
                    sharedViewModel.setWantedMarket(listOf("mercadona"))
                    viewModel.navigateToSuggestionScreen(
                        navController,
                        sharedViewModel.selectedList.value!!.uuid
                    )
                }) {
                    Text(text = "Sugerencias")
                }

            }
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
            .height(110.dp)
            .padding(8.dp)
            .clickable(onClick = {
                sharedViewModel.clearCurrentProduct()
                sharedViewModel.setCurrentProduct(product)
                sharedViewModel.setProductHistoryDetails()
                viewModel.navigateToProductDetailsScreen(navController)
            })

    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberImagePainter(data = product.image_url),
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
                Text(text = product.name)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "Price: ${product.price} €")
                Text(text = "Store: ${product.store_name}")
            }

            Button(
                onClick = {
                    viewModel.deleteProduct(product)
                    onDelete(product)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .size(69.dp) // Adjust size as needed
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete product",
                    modifier = Modifier.size(24.dp) // Adjust size as needed
                )
            }
        }
    }
}
