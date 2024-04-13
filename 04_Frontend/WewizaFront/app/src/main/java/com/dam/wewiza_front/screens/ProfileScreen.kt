@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.dam.wewiza_front.R
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.navigation.AppScreens
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.ProfileScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    viewModel: ProfileScreenViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxSize()) {
                ProfileTopBar(navController)
            }

        },
        bottomBar = {
            Constants.BottomMenu(navController)
        }
    ) {
        MyLightTheme {
            ProfileScreenBodyContent(viewModel, navController)
        }
    }
}

@Composable
fun ProfileScreenBodyContent(
    viewModel: ProfileScreenViewModel,
    navController: NavController
) {
    val context = LocalContext.current

    var isLoading = viewModel.isLoading.value
    val profile = viewModel.profile.value

    while (isLoading) {
        if (!viewModel.isLoading.value) {
            isLoading = false
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf(profile?.imageUrl) }
    var username by remember { mutableStateOf(profile?.name) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(70.dp))


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePicture(selectedImage)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(text = username ?: "DefaultUsername", modifier = Modifier.padding(16.dp))
            }
        }

        // Botones
        ProfileButton(
            text = "Editar Perfil",
            icon = R.drawable.baseline_edit_24,
        ) { showDialog = true }

        ProfileButton(
            text = "Mis Listas",
            icon = R.drawable.baseline_format_list_bulleted_24,
        ) { viewModel.navigateToMyListScreen(navController) }

        ProfileButton(
            text = "Soporte Técnico",
            icon = R.drawable.baseline_contact_support_24,
        ) { viewModel.navigateToCustomerSupportScreen(navController) }


        if (showDialog) {
            EditProfileDialog(
                onDismissRequest = { showDialog = false },
                onImageClick = { TODO("Need to implement the imagen picker function") },
                onConfirmClick = {
                    viewModel.updateProfileDataOnFiresbase(selectedImage, username, context)
                    showDialog = false
                },
                selectedImage = selectedImage,
                username = username ?: "DefaultUsername",
                onUsernameChange = { newUsername -> username = newUsername }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    onDismissRequest: () -> Unit,
    onImageClick: () -> Unit,
    onConfirmClick: () -> Unit,
    selectedImage: String?,
    username: String,
    onUsernameChange: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .size(300.dp, 600.dp)
                .padding(top = 70.dp)
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.clickable(onClick = onImageClick)) {
                ProfilePicture(selectedImage)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = username, onValueChange = onUsernameChange)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onConfirmClick) {
                Text("Confirmar")
            }
            Spacer(modifier = Modifier.height(16.dp)) // Agrega espacio entre los botones
            Button(onClick = onDismissRequest) { // Botón de cancelar que cierra el diálogo
                Text("Cancelar")
            }
        }
    }
}


@Composable
fun ProfileTopBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colorScheme.surface) // Color de fondo
    ) {
        Text(text = "Wewiza") //Insertar logo de letras

        // Icono de ajustes
        Icon(
            painter = painterResource(id = R.drawable.settings),
            contentDescription = "Settings",
            modifier = Modifier
                .clickable { navController.navigate(AppScreens.SettingsScreen.route) }
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp) // Margen desde el borde superior y derecho
                .size(40.dp) // Tamaño del icono
        )
    }
}

@Composable
fun ProfilePicture(imageUrl: String?) {

    val defaultImage = painterResource(id = R.drawable.defaultprofilepic)
    val painter: Painter = if (imageUrl.isNullOrEmpty()) {
        defaultImage
    } else {
        rememberImagePainter(
            data = imageUrl,
            builder = {
                crossfade(true)
                placeholder(R.drawable.defaultprofilepic)
            }
        )
    }

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ProfileButton(text: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = icon), contentDescription = "profile pic")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White)
        }
    }
}




