package com.dam.wewiza_front.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dam.wewiza_front.R
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
    Box(modifier = Modifier.run {
        background(Color(0xFFD0C2DC))
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.provisional_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(300.dp)
            )
            Text(
                "¡Registrate en Wewiza!", style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = Butler,
                    color = Color(0xFF2E2C31)
                )
            )
        }
    }
}
