@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.widget.CheckBox
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ProductScreenViewModel


var showDialog: MutableState<Boolean> = mutableStateOf(false)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    productScreenViewModel: ProductScreenViewModel,
    navController: NavHostController
) {
    var searchText by remember { mutableStateOf("") }
    var isMercadonaChecked by remember { mutableStateOf(true) }
    var isAhorramasChecked by remember { mutableStateOf(true) }
    var isCarrefourChecked by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            Constants.BottomMenu(navController)
        }
    ) {
        MyLightTheme {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .padding(top = 10.dp)
            ) {
                ProductsSearchBar(
                    "Buscar producto",
                    searchText = searchText,
                    onValueChange = { searchText = it },
                    isMercadonaChecked = isMercadonaChecked,
                    onMercadonaCheckedChange = { isMercadonaChecked = it },
                    isAhorramasChecked = isAhorramasChecked,
                    onAhorramasCheckedChange = { isAhorramasChecked = it },
                    isCarrefourChecked = isCarrefourChecked,
                    onCarrefourCheckedChange = { isCarrefourChecked = it }
                )
                ProductScreenBodyContent(
                    productScreenViewModel,
                    navController,
                    searchText,
                    isMercadonaChecked,
                    isAhorramasChecked,
                    isCarrefourChecked
                )
            }
        }
    }
}

@Composable
fun ProductScreenBodyContent(
    viewModel: ProductScreenViewModel,
    navController: NavHostController,
    searchText: String,
    isMercadonaChecked: Boolean,
    isAhorramasChecked: Boolean,
    isCarrefourChecked: Boolean
) {
    val products = viewModel.allProductsList
    val filteredProducts = products.filter { it.name.contains(searchText, ignoreCase = true) }
    ProductGrid(products = filteredProducts, isMercadonaChecked, isAhorramasChecked, isCarrefourChecked)
}

@Composable
fun ProductGrid(
    products: List<Product>,
    isMercadonaChecked: Boolean,
    isAhorramasChecked: Boolean,
    isCarrefourChecked: Boolean
) {
    val filteredProducts = products.filter {
        (it.store_name == "Mercadona" && isMercadonaChecked) ||
                (it.store_name == "Ahorramas" && isAhorramasChecked) ||
                (it.store_name == "Carrefour" && isCarrefourChecked)
    }
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        content = {
            items(filteredProducts.size) { index ->
                ProductItem(filteredProducts[index])
            }
        }
    )
}

@Composable
fun ProductItem(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = product.image_url),
                contentDescription = product.name,
                modifier = Modifier
                    .size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre la imagen y el texto
            Column {
                Text(text = product.name)
                Text(text = "${product.price} €")
                Text(text = product.store_name)
            }
        }
    }
}

@Composable
fun ProductsSearchBar(
    labelText: String,
    searchText: String,
    onValueChange: (String) -> Unit,
    isMercadonaChecked: Boolean,
    onMercadonaCheckedChange: (Boolean) -> Unit,
    isAhorramasChecked: Boolean,
    onAhorramasCheckedChange: (Boolean) -> Unit,
    isCarrefourChecked: Boolean,
    onCarrefourCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = searchText,
            onValueChange = onValueChange,
            label = { Text(labelText) },
            modifier = Modifier
                .padding(8.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.baseline_filter_list_24),
            contentDescription = "Filter",
            modifier = Modifier
                .size(70.dp)
                .clickable { showDialog.value = true },
            alignment = Alignment.Center
        )
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false  },
            title = { Text(text = "Filtro de productos") },
            text = {
                Text(text = "Mostrar productos de:")
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Box(modifier = Modifier.width(100.dp)) {
                            Text("Mercadona")
                        }
                        Checkbox(
                            checked = isMercadonaChecked,
                            onCheckedChange = onMercadonaCheckedChange,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Box(modifier = Modifier.width(100.dp)) {
                            Text("Ahorramas")
                        }

                        Checkbox(
                            checked = isAhorramasChecked,
                            onCheckedChange = onAhorramasCheckedChange,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        Box(modifier = Modifier.width(100.dp)) {
                            Text("Carrefour")
                        }

                        Checkbox(
                            checked = isCarrefourChecked,
                            onCheckedChange = onCarrefourCheckedChange,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false  }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}