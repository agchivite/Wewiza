@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.SuggestionScreenViewModel
import kotlinx.coroutines.launch


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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 70.dp)
    ) {
        val selectedList = viewModel.sharedViewModel.selectedList.value
        val wantedMarket = viewModel.sharedViewModel.getWantedMarket()
        var productsList by remember { mutableStateOf(emptyList<Product>()) }
        var suggestions by viewModel.suggestions

        Column(modifier = Modifier.fillMaxSize()) {
            LaunchedEffect(selectedList!!.products.isNotEmpty()) {
                suggestions =
                    viewModel.getSuggestions(wantedMarket, selectedList.products).toMutableMap()
                val products = viewModel.getProductsFromList(selectedList)
                productsList = products
                Log.d("ListScreen", "Products: $products")
            }
            if (selectedList.products.isNotEmpty()) {
                if (suggestions.isEmpty() || productsList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Este proceso puede tardar varios minutos...",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Black
                        )
                    }
                } else {
                    val combinedMap = productsList.associateWith { product ->
                        suggestions[product.uuid] ?: emptyList()
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 10.dp)
                    ) {
                        items(combinedMap.entries.toList().size) { index ->
                            val entry = combinedMap.entries.toList()[index]
                            ProductItem(entry, viewModel)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        AcceptSuggestionsButton(viewModel, navController, selectedList!!.uuid)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_simple),
                        contentDescription = "logo"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Añade un producto a la lista para recibir sugerencias",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black
                    )
                }
            }
        }


    }
}


@Composable
fun AcceptSuggestionsButton(
    viewModel: SuggestionScreenViewModel,
    navController: NavController,
    shoppingListUUID: String
) {
    Button(
        onClick = {
            viewModel.acceptSuggestions(shoppingListUUID)
            viewModel.navigateToMyListsScreen(navController)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(50), // Bordes redondeados
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary, // Color de fondo del botón
            contentColor = Color.White // Color del texto del botón
        )
    ) {
        Text(
            text = "Aceptar sugerencias",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}


@Composable
fun ProductItem(entry: Map.Entry<Product, List<Product>>, viewModel: SuggestionScreenViewModel) {
    val product = entry.key
    val suggestedProducts = entry.value

    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(0.dp)
                    .width(130.dp)
            ) {
                Box {
                    Column(
                        modifier = Modifier
                            .padding(8.dp), // Padding inside the card
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = product.image_url),
                            contentDescription = "Product Image",
                            modifier = Modifier
                                .size(70.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FirsNeue,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Precio: ${product.price} €",
                            fontFamily = FirsNeue,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Precio/medida: ${product.price_by_standard_measure} €/${
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
                            }",
                            fontFamily = FirsNeue,
                            fontSize = 10.sp
                        )
                    }

                    // Store Icon
                    when (product.store_name.trim().lowercase()) {
                        "mercadona" -> {
                            Image(
                                painter = painterResource(id = R.drawable.mercadona_logo),
                                contentDescription = "mercadona",
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.TopStart)
                                    .padding(2.dp)
                            )
                        }

                        "ahorramas" -> {
                            Image(
                                painter = painterResource(id = R.drawable.ahorramas),
                                contentDescription = "ahorramas",
                                modifier = Modifier
                                    .size(50.dp)
                                    .align(Alignment.TopStart)
                                    .padding(2.dp)
                            )
                        }

                        "carrefour" -> {
                            Image(
                                painter = rememberAsyncImagePainter(model = product.store_image_url),
                                contentDescription = "carrefour",
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.TopStart)
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.padding(horizontal = 0.dp)
            ) {
                Text(text = "→", fontSize = 30.sp)
            }

            if (suggestedProducts.isEmpty()) {
                Text(
                    text = "No hay sugerencias para este producto",
                    modifier = Modifier.weight(2f),
                    color = Color.Black
                )
            } else {
                LazyRow(
                    modifier = Modifier.weight(2f)
                ) {
                    items(suggestedProducts.size) { index ->
                        SuggestedProductItem(suggestedProducts[index], viewModel, product.uuid)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SuggestedProductItem(product: Product, viewModel: SuggestionScreenViewModel, uuid: String) {
    var isAccepted by remember { mutableStateOf(false) }

    // Animations for scale and Y position
    val scale = remember { Animatable(1f) }
    val offsetY = remember { Animatable(0f) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(isAccepted) {
        if (isAccepted) {
            // Center the element in the LazyRow first
            launch {
                lazyListState.animateScrollToItem(lazyListState.layoutInfo.visibleItemsInfo.indexOfFirst { it.key == product.uuid })
                bringIntoViewRequester.bringIntoView()
            }
            // Then execute the animation
            launch {
                offsetY.animateTo(
                    targetValue = -30f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )
            }
            launch {
                scale.animateTo(
                    targetValue = 1.1f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )
                scale.animateTo(
                    targetValue = 0.75f,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(180.dp)
            .padding(start = 3.dp)
            .graphicsLayer {
                translationY = offsetY.value
                scaleX = scale.value
                scaleY = scale.value
            }
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        Box {


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = product.image_url),
                    contentDescription = "Suggested Product Image",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.name, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp),
                    fontFamily = FirsNeue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Precio: ${product.price} €", fontFamily = FirsNeue, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Precio/medida: ${product.price_by_standard_measure} €/${
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
                    }", fontFamily = FirsNeue, fontSize = 12.sp
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel Icon
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_cancel_24),
                        contentDescription = "cancel",
                        modifier = Modifier
                            .size(60.dp)
                            .clickable(onClick = {
                                viewModel.deleteProductFromSuggestions(product, uuid)
                            })
                            .padding(8.dp),
                        tint = Color(0xFFD32F2F)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Accept Icon
                    Icon(
                        painter = painterResource(id = R.drawable.accept),
                        contentDescription = "accept",
                        modifier = Modifier
                            .size(60.dp)
                            .clickable(onClick = {
                                isAccepted = true
                                viewModel.addProductToChoosenProductsList(product, uuid)
                            })
                            .padding(8.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            }

            // Store Icon
            when (product.store_name.trim().lowercase()) {
                "mercadona" -> {
                    Image(
                        painter = painterResource(id = R.drawable.mercadona_logo),
                        contentDescription = "mercadona",
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.TopStart)
                            .padding(2.dp)
                    )
                }

                "ahorramas" -> {
                    Image(
                        painter = painterResource(id = R.drawable.ahorramas),
                        contentDescription = "ahorramas",
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.TopStart)
                            .padding(2.dp)
                    )
                }

                "carrefour" -> {
                    Image(
                        painter = rememberAsyncImagePainter(model = product.store_image_url),
                        contentDescription = "carrefour",
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.TopStart)
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}



