package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ListScreenViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(listScreenViewModel: ListScreenViewModel, navController: NavHostController) {
    val selectedProductsIds = sharedViewModel.selectedList.value
    val productsList = remember { mutableStateOf(emptyList<Product>()) }
    Log.d("ListScreen", "Selected Products: ${selectedProductsIds!!.products.size}")
    var loading by  remember { mutableStateOf(false)}

    if (selectedProductsIds!!.products.size > 0) {

        LaunchedEffect(selectedProductsIds) {
            loading = true
            val products = listScreenViewModel.getProductsFromList(selectedProductsIds)
            productsList.value = products
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
                ListScreenBodyContent(listScreenViewModel, navController, productsList)
            }
        }
    }
}


@Composable
fun ListScreenBodyContent(
    viewModel: ListScreenViewModel,
    navController: NavHostController,
    productsList: MutableState<List<Product>>
) {
    val productsList = productsList.value

    Column(
        modifier = Modifier
            .padding(top = 70.dp)
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        LazyColumn {
            items(productsList.size) { index ->
                ListProductItem(product = productsList[index])
            }
        }
    }

}


@Composable
fun ListProductItem(product: Product) {
    Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth().height(110.dp).padding(8.dp)) {
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