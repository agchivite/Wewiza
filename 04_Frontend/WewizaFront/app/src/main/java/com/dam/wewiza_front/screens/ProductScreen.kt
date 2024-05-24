@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ProductScreenViewModel
import com.dam.wewiza_front.viewModels.SharedViewModel
import kotlinx.coroutines.delay

var showDialog: MutableState<Boolean> = mutableStateOf(false)
var sharedViewModel = SharedViewModel.instance

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
    var selectedCategories by remember {
        sharedViewModel.selectedCategories
    }
    var priceSortOrder by remember { mutableStateOf("Ninguno") }

    Scaffold(
        bottomBar = {
            Constants.BottomMenu(navController)
        }
    ) {
        MyLightTheme {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
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
                    onCarrefourCheckedChange = { isCarrefourChecked = it },
                    selectedCategories,
                    priceSortOrder,
                    onPriceSortOrderChange = { priceSortOrder = it }
                )
                ProductScreenBodyContent(
                    productScreenViewModel,
                    navController,
                    searchText,
                    isMercadonaChecked,
                    isAhorramasChecked,
                    isCarrefourChecked,
                    selectedCategories,
                    priceSortOrder
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
    isCarrefourChecked: Boolean,
    selectedCategories: MutableState<MutableMap<String, Boolean>>,
    priceSortOrder: String
) {
    val products by viewModel.allProductsList.collectAsState()
    val isProductsLoading by viewModel.isProductsLoading.collectAsState()

    val filteredProducts = products
        .filter { it.name.contains(searchText, ignoreCase = true) }
        .filter {
            ((it.store_name == "Mercadona" && isMercadonaChecked) ||
                    (it.store_name == "Ahorramas" && isAhorramasChecked) ||
                    (it.store_name == "Carrefour" && isCarrefourChecked)) && (
                    (selectedCategories.value[it.category_id] == true))
        }
        .sortedWith(
            when (priceSortOrder) {
                "Ascendente" -> compareBy { it.price }
                "Descendente" -> compareByDescending { it.price }
                else -> compareBy { it.uuid } // Default order if no sorting is selected
            }
        )


    if (isProductsLoading) {
        Box(modifier = Modifier.fillMaxSize()){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }else{
        LaunchedEffect(products){
            delay(100)
        }
        ProductGrid(
            products = filteredProducts,
            isMercadonaChecked,
            isAhorramasChecked,
            isCarrefourChecked,
            selectedCategories,
            viewModel,
            navController
        )
    }


}

@Composable
fun ProductGrid(
    products: List<Product>,
    isMercadonaChecked: Boolean,
    isAhorramasChecked: Boolean,
    isCarrefourChecked: Boolean,
    selectedCategories: MutableState<MutableMap<String, Boolean>>,
    viewModel: ProductScreenViewModel,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.padding(bottom = 70.dp),
        contentPadding = PaddingValues(8.dp),
        content = {
            items(products.size) { index ->
                ProductItem(products[index], viewModel, navController)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    product: Product,
    viewModel: ProductScreenViewModel,
    navController: NavHostController
) {
    Box(modifier = Modifier.padding(8.dp)){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = {
                sharedViewModel.clearCurrentProduct()
                sharedViewModel.setCurrentProduct(product)
                sharedViewModel.setProductHistoryDetails()
                viewModel.navigateToProductDetailsScreen(navController)
            }
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
                    Text(text = product.name, fontFamily = FirsNeue, fontWeight = FontWeight.SemiBold)
                    Text(text = "${product.price} €", modifier = Modifier.padding(top = 10.dp), fontFamily = FirsNeue)
                }
            }
        }
        if (product.store_name.lowercase().trim() == "mercadona") {
            Image(
                painter = painterResource(id = R.drawable.mercadona_logo),
                contentDescription = "Imagen de la tienda",
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            )
        } else if (product.store_name.lowercase().trim() == "ahorramas") {
            Image(
                painter = painterResource(id = R.drawable.ahorramas),
                contentDescription = "Ahorramas",
                modifier = Modifier
                    .size(75.dp)
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            )
        } else {
            Image(
                painter = rememberImagePainter(data = product.store_image_url),
                contentDescription = "Imagen de la tienda",
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
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
    onCarrefourCheckedChange: (Boolean) -> Unit,
    selectedCategories: MutableState<MutableMap<String, Boolean>>,
    priceSortOrder: String,
    onPriceSortOrderChange: (String) -> Unit
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
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Filtro de productos") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
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
                    Text(text = "Categorias: ")
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        sharedViewModel.getCategories().forEach { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 20.dp)
                            ) {
                                Box(modifier = Modifier.width(100.dp)) {
                                    Text(category.name)
                                }
                                Checkbox(
                                    checked = selectedCategories.value[category.id] ?: false,
                                    onCheckedChange = { isChecked ->
                                        selectedCategories.value =
                                            selectedCategories.value.toMutableMap()
                                                .also { it[category.id] = isChecked }
                                    },
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        Button(onClick = {
                            val newMap = selectedCategories.value.toMutableMap()
                            newMap.keys.forEach { newMap[it] = true }
                            selectedCategories.value = newMap
                        }, modifier = Modifier.padding(8.dp)) {
                            Text("Seleccionar todas las categorías")
                        }

                        Button(onClick = {
                            val newMap = selectedCategories.value.toMutableMap()
                            newMap.keys.forEach { newMap[it] = false }
                            selectedCategories.value = newMap
                        }) {
                            Text("Deseleccionar todas las categorías")
                        }
                    }

                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { expanded = !expanded }) {
                                Text("Ordenar por precio: $priceSortOrder")
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(onClick = {
                                    onPriceSortOrderChange("Ascendente")
                                    expanded = false
                                }, text = {
                                    Text("Ordenar por precio: Ascendente")
                                }
                                )
                                DropdownMenuItem(onClick = {
                                    onPriceSortOrderChange("Descendente")
                                    expanded = false
                                }, text = {
                                    Text("Ordenar por precio: Descendente")
                                }
                                )
                                DropdownMenuItem(onClick = {
                                    onPriceSortOrderChange("Ninguno")
                                    expanded = false
                                }, text = {
                                    Text("Ordenar por precio: Ninguno")
                                }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}
