@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam.wewiza_front.MainActivity
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.SettingsScrennViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    viewModel: SettingsScrennViewModel,
    navController: NavController,
    mainActivity: MainActivity
) {

    Scaffold(topBar = {
        Constants.TopBarWithLogo(navController)
    }) {
        MyLightTheme {
            SettingsScreenBodyContent(viewModel, navController, mainActivity)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenBodyContent(
    viewModel: SettingsScrennViewModel,
    navController: NavController,
    mainActivity: MainActivity
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 70.dp)
    ) {
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(20.dp))


        Divider(color = Color.Black, thickness = 1.dp)


        Spacer(modifier = Modifier.height(20.dp))
        AboutUsSection(navController)
        Spacer(modifier = Modifier.height(20.dp))

        Divider(color = Color.Black, thickness = 1.dp)

        Spacer(modifier = Modifier.height(20.dp))
        SignOutSection(viewModel, context, navController, mainActivity)
        Spacer(modifier = Modifier.height(20.dp))


        Divider(color = Color.Black, thickness = 1.dp)


        Spacer(modifier = Modifier.height(20.dp))
        DeleteAccountSection(viewModel, navController, context, mainActivity)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AboutUsSection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { navController.navigate(AppScreens.AboutUsScreen.route) }) {
            Text("Sobre nosotros")
        }
    }

}

@Composable
fun DeleteAccountSection(
    viewModel: SettingsScrennViewModel,
    navController: NavController,
    context: Context,
    mainActivity: MainActivity
) {

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                viewModel.deleteAccount(navController, context, mainActivity)

            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Eliminar cuenta")
        }
    }

}

@Composable
fun SignOutSection(
    viewModel: SettingsScrennViewModel,
    context: Context,
    navController: NavController,
    mainActivity: MainActivity
){
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                viewModel.signOut(navController, context, mainActivity)

            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Cerrar Sesión")
        }
    }
}

