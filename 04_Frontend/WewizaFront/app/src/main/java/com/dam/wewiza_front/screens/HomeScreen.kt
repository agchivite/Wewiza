package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.constants.Constants.BottomMenu
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.HomeScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navController: NavController
) {
    Scaffold(
        bottomBar = {
            Constants.BottomMenu(navController)
        }
    ) {

        MyLightTheme {
            HomeBodyContent(navController, viewModel)
        }
    }
}

@Composable
fun HomeBodyContent(
    navController: NavController,
    viewModel: HomeScreenViewModel,
) {


}

