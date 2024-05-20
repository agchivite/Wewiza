package com.dam.wewiza_front.screens


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dam.wewiza_front.Formatter.MonthAxisValueFormatter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
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
        val entries = viewModel.prepareChartData(productHistoryDetails)
        Log.d("ProductDetailsScreen", "Entries: $entries")
        LineChartView(entries)
    } else {
        CircularProgressIndicator()
    }
}

@Composable
fun LineChartView(entries: List<Entry>) {
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
                    setTouchEnabled(true)
                    setDrawGridBackground(false)
                    isDragEnabled = false
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    axisLeft.setDrawGridLines(true)
                    axisLeft.setDrawAxisLine(false)
                    axisRight.isEnabled = false
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = MonthAxisValueFormatter()
                        setDrawLabels(true)
                        labelRotationAngle =
                            -45f // Rotar el texto en -45 grados para que aparezca en diagonal
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
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
    ) {
        val showDialog = remember { mutableStateOf(false) }
        val auth = FirebaseAuth.getInstance()

        Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {

            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Nombre: ${currentProduct.name}")
                Text(text = "Tienda: ${currentProduct.store_name}")
                Text(text = "Precio: ${currentProduct.price} €")
            }

            Row(modifier = Modifier.padding(20.dp)) {
                Button(onClick = {
                    viewModel.unlikeProduct(currentProduct.uuid)

                    viewModel.updateUserReviews()
                }) {
                    Text(text = "-")
                }
                Text(text = currentProduct.num_likes.toString())

                Button(onClick = {
                    viewModel.likeProduct(currentProduct.uuid)
                    viewModel.updateUserReviews()
                }) {
                    Text(text = "+")
                }
                Spacer(modifier = Modifier.width(150.dp))

                Column() {
                    Button(onClick = { showDialog.value = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_shopping_cart_24),
                            contentDescription = "addToList"
                        )
                    }
                }

                //   val availableLists = sharedViewModel.getLocalShoppingLists().value
                val context = LocalContext.current

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
                            }else {
                                Column {
                                    availableLists.value.forEach { shoppingList ->
                                        Button(onClick = {
                                            viewModel.addProductToList(
                                                shoppingList.uuid,
                                                currentProduct.uuid,
                                                context
                                            )
                                            showDialog.value = false
                                        }) {
                                            Text(shoppingList.name)
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showDialog.value = false }) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog.value = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
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
