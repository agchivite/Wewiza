package com.dam.wewiza_front.screens


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.dam.wewiza_front.Formatter.MonthAxisValueFormatter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ProductDetailsScreenViewModel
import com.dam.wewiza_front.viewModels.SharedViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.clickable


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
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 70.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        PhotoField(currentProduct)
        ProductDetailsFields(currentProduct, viewModel)
        GraphicField(viewModel)
    }

}

@Composable
fun GraphicField(viewModel: ProductDetailsScreenViewModel) {
    val productHistoryDetails = viewModel.getProductHistoryDetails().sortedBy { it.date_created }
    Log.d("ProductDetailsScreen", "ProductHistoryDetails: $productHistoryDetails")
    Log.d("ProductDetailsScreen", "CurrentProduct: ${sharedViewModel.getCurrentProduct()}")
    
    if (
        productHistoryDetails.isNotEmpty() &&
        productHistoryDetails.last().uuid == sharedViewModel.getCurrentProduct().uuid
    ) {
        val (entries, monthMap) = viewModel.prepareChartData(productHistoryDetails)
        Log.d("ProductDetailsScreen", "Entries: $entries")
        LineChartView(entries, monthMap)
    } else {
        CircularProgressIndicator()
    }
}


@Composable
fun LineChartView(entries: List<Entry>, monthMap: Map<Float, String>) {
    Column(modifier = Modifier
        .padding(50.dp)
        .padding(bottom = 40.dp)) {
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    setViewPortOffsets(0f, 0f, 0f, 0f)
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    description = Description().apply {
                        text = ""
                    }
                    setTouchEnabled(false)
                    setDrawGridBackground(false)
                    isDragEnabled = false
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    axisLeft.setDrawGridLines(true)
                    axisLeft.setDrawAxisLine(false)
                    axisRight.isEnabled = false
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = MonthAxisValueFormatter(monthMap)
                        setDrawLabels(true)
                        granularity = 1f // Asegura que solo se muestre una etiqueta por valor
                        labelRotationAngle = -45f
                    }

                    val dataSet = LineDataSet(entries, "Precios a lo largo del tiempo").apply {
                        color = android.graphics.Color.RED
                        valueTextColor = android.graphics.Color.BLACK
                        lineWidth = 1.5f
                        setDrawCircles(true)
                        setDrawValues(true)
                    }

                    val lineData = LineData(dataSet)
                    data = lineData
                    animateX(1500)
                    invalidate()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ProductDetailsFields(currentProduct: Product, viewModel: ProductDetailsScreenViewModel) {
    var buttonsEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = currentProduct.name,
                    fontFamily = FirsNeue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Precio: ${currentProduct.price} €",
                    fontFamily = FirsNeue,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
            when (currentProduct.store_name.lowercase().trim()) {
                "mercadona" -> Image(
                    painter = painterResource(id = R.drawable.mercadona_logo),
                    contentDescription = "Imagen de la tienda",
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                )
                "ahorramas" -> Image(
                    painter = painterResource(id = R.drawable.ahorramas),
                    contentDescription = "Ahorramas",
                    modifier = Modifier
                        .size(75.dp)
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                )
                else -> Image(
                    painter = rememberImagePainter(data = currentProduct.store_image_url),
                    contentDescription = "Imagen de la tienda",
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                )
            }
        }

        Row(modifier = Modifier.padding(20.dp)) {
            Button(
                onClick = {
                    if (buttonsEnabled) {
                        buttonsEnabled = false
                        viewModel.unlikeProductWithCallback(currentProduct.uuid) { result ->
                            // Manejar el resultado del unlike
                            buttonsEnabled = true
                            if (result) {
                                viewModel.updateUserReviews()
                                currentProduct.num_likes -= 1
                                Toast.makeText(context, "No te gusta...", Toast.LENGTH_SHORT).show()
                                Log.d("ProductDetailsScreen", "LikesNumber: ${currentProduct.num_likes}")
                            } else {
                                Toast.makeText(context, "Ya diste no me gusta a este producto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = buttonsEnabled
            ) {
                Text(text = "-")
            }

            Text(
                text = currentProduct.num_likes.toString(),
                fontFamily = FirsNeue,
                fontSize = 18.sp,
                color = Color.Black
            )

            Button(
                onClick = {
                    if (buttonsEnabled) {
                        buttonsEnabled = false
                        viewModel.likeProductWithCallback(currentProduct.uuid) { result ->
                            // Manejar el resultado del like
                            buttonsEnabled = true
                            if (result) {
                                viewModel.updateUserReviews()
                                currentProduct.num_likes += 1
                                Toast.makeText(context, "Te gusta!", Toast.LENGTH_SHORT).show()
                                Log.d("ProductDetailsScreen", "LikesNumber: ${currentProduct.num_likes}")
                            } else {
                                Toast.makeText(context, "Ya diste me gusta a este producto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = buttonsEnabled
            ) {
                Text(text = "+")
            }

            Spacer(modifier = Modifier.width(150.dp))

            Column {
                Button(onClick = { showDialog.value = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_shopping_cart_24),
                        contentDescription = "addToList"
                    )
                }
            }

            if (showDialog.value) {
                val availableLists = viewModel.recoverProfileData(auth.currentUser!!)
                    .collectAsState(initial = emptyList())
                Log.d("ProductDetailsScreen", "Profile: ${availableLists.value}")

                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text(text = "Selecciona la lista a la que quieres agregar este producto") },
                    text = {
                        if (availableLists.value.isEmpty()) {
                            Text("Parece que no tienes ninguna lista... ¡Crea tu primer lista!")
                        } else {
                            Column(modifier = Modifier.height(300.dp)) {

                                LazyColumn(content = {
                                    items(availableLists.value.size) { index ->
                                        ShoppingListItem(availableLists.value[index], viewModel, currentProduct, context)
                                    }
                                })
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showDialog.value = false }) {
                            Text("Aceptar")
                        }
                    },
                )
            }
        }
    }
}


@Composable
fun ShoppingListItem(
    shoppingList: ShoppingList,
    viewModel: ProductDetailsScreenViewModel,
    currentProduct: Product,
    context: Context

) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                viewModel.addProductToList(
                    shoppingList.uuid,
                    currentProduct.uuid,
                    context,
                    shoppingList.name
                )
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = shoppingList.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Productos: ${shoppingList.products.size}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
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
