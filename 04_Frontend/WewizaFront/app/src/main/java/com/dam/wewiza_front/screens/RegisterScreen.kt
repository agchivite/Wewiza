package com.dam.wewiza_front.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.dam.wewiza_front.viewModels.RegisterScreenViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterScreenViewModel,
    navController: NavController
){
    Column() {
        RegisterScreenBodyContent()
    }
}

@Composable
fun RegisterScreenBodyContent() {
    Text(text="RegisterScreen")
}
