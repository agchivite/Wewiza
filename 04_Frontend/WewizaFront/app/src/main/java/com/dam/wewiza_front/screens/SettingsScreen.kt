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
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.SettingsScrennViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    viewModel: SettingsScrennViewModel,
    navController: NavController
) {

    Scaffold(topBar = {
        Constants.TopBarWithLogo(navController)
    }) {
        MyLightTheme {
            SettingsScreenBodyContent(viewModel, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenBodyContent(viewModel: SettingsScrennViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 70.dp)
    ) {
        val context = LocalContext.current


        Spacer(modifier = Modifier.height(20.dp))
        ThemeSelector()
        Spacer(modifier = Modifier.height(20.dp))


        Divider(color = Color.Black, thickness = 1.dp)


        Spacer(modifier = Modifier.height(20.dp))
        AboutUsSection(navController)
        Spacer(modifier = Modifier.height(20.dp))


        Divider(color = Color.Black, thickness = 1.dp)


        Spacer(modifier = Modifier.height(20.dp))
        DeleteAccountSection(viewModel, navController, context)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ThemeSelector() {
    var expanded by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("Claro") } // Estado para almacenar el tema seleccionado

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tema: $selectedTheme") // Mostrar el tema seleccionado al lado del texto "Tema:"
            Spacer(modifier = Modifier.width(16.dp))

            Box {
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(Icons.Outlined.ArrowDropDown, contentDescription = "")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(120.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text("Claro")
                        },
                        onClick = {
                            selectedTheme = "Claro" // Actualizar el tema seleccionado
                            expanded = !expanded
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Oscuro")
                        },
                        onClick = {
                            selectedTheme = "Oscuro" // Actualizar el tema seleccionado
                            expanded = !expanded
                        }
                    )
                }
            }
        }
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
    context: Context
) {

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                viewModel.deleteAccount(navController, context)

            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Eliminar cuenta")
        }
    }

}

