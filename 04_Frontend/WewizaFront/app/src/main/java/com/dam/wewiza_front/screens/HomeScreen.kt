package com.dam.wewiza_front.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam.wewiza_front.R


@Composable
fun HomeScreen(
    navController: NavController
) {
    TopBar(navController)
}

@Composable
fun TopBar(navController: NavController) {
    Box(modifier = Modifier.run {
        fillMaxWidth()
        height(100.dp)
        background(Color.LightGray)
    }) {
        Row {
            Box(modifier = Modifier.size(20.dp)){
                Image(painter = painterResource(id = R.drawable.provisional_logo), contentDescription ="Logo",  )
            }
            Text(text = "Bienvenido a Wewiza!!")
        }
    }
}

