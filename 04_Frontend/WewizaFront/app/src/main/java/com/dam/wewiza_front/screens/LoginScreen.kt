package com.dam.wewiza_front.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.dam.wewiza_front.viewModels.LoginScreenViewModel

@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel,
    navController: NavController
){

   Column() {
       LoginScreenBodyContent()
   }
}

@Composable
fun LoginScreenBodyContent() {
    Text(text="LoginScreen")
}

