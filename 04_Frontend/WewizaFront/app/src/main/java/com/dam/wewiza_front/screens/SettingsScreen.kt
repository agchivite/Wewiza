package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
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
            .background(MaterialTheme.colorScheme.surface)
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
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                showDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Eliminar cuenta")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmación") },
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        viewModel.deleteAccount(navController, context, mainActivity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SignOutSection(
    viewModel: SettingsScrennViewModel,
    context: Context,
    navController: NavController,
    mainActivity: MainActivity
){
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                showDialog = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Cerrar Sesión")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirmación") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        viewModel.signOut(navController, context, mainActivity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
