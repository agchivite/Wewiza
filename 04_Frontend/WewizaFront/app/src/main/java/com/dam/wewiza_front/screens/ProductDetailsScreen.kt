package com.dam.wewiza_front.screens


import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ProductDetailsScreenViewModel


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
            ProductDetailsScreenBodyContent(viewModel, navController)
        }
    }
}

@Composable
fun ProductDetailsScreenBodyContent(
    viewModel: ProductDetailsScreenViewModel,
    navController: NavController
) {

}
