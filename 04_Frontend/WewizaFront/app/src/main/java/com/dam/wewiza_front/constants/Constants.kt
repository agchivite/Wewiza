package com.dam.wewiza_front.constants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam.wewiza_front.R
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.ui.theme.MyLightTheme

object Constants {
    const val FIREBASE_CLIENT_ID =
        "348348678536-sr4horn983c8c77q0ab122ettjmovtuf.apps.googleusercontent.com"


    @Composable
    fun BottomMenu(navController: NavController) {
        MyLightTheme {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(MaterialTheme.colorScheme.onTertiary),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BottomMenuItem(
                        icon = R.drawable.destacados,
                        text = "Destacado",
                        onClick = { navController.navigate(AppScreens.HomeScreen.route) }
                    )
                    BottomMenuItem(
                        icon = R.drawable.buscador,
                        text = "Buscador",
                        onClick = { navController.navigate(AppScreens.ProductScreen.route) }
                    )
                    BottomMenuItem(
                        icon = R.drawable.mis_listas,
                        text = "Mis listas",
                        onClick = { navController.navigate(AppScreens.MyListScreen.route) }
                    )
                    BottomMenuItem(
                        icon = R.drawable.profile,
                        text = "Mi perfil",
                        onClick = { navController.navigate(AppScreens.ProfileScreen.route) }
                    )
                }
            }
        }
    }

    @Composable
    fun BottomMenuItem(
        icon: Int,
        text: String,
        onClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = text,
                color = Color.White,
            )
        }
    }

    @Composable
    fun TopBarWithLogo(navController: NavController) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(color = Color(0xFF7386C0)), // Color de fondo
            contentAlignment = Alignment.Center
        ) {

            IconButton(onClick = {
                navController.popBackStack()
            }, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "back", tint = Color.Black)
            }
            Image(painter = painterResource(id = R.drawable.logo_letras), contentDescription = "logo", modifier = Modifier.size(100.dp))

        }
    }


}
