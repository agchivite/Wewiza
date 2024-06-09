@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.core.net.toUri
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



    // Save original values to restore them if the user cancels the dialog
    val originalSelectedImage by rememberSaveable { mutableStateOf(selectedImage) }
    val originalUsername by rememberSaveable { mutableStateOf(username) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(70.dp))


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePicture(selectedImage, context, false) {
                Toast.makeText(context, "Selecciona la opción de editar", Toast.LENGTH_LONG).show()
            }
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
                onDismissRequest = {
                    //restoring original values of name and profile pic
                    selectedImage = originalSelectedImage
                    username = originalUsername
                    showDialog = false
                },
                onConfirmClick = {
                    viewModel.updateProfileDataOnFiresbase(originalSelectedImage?.toUri(), selectedImage?.toUri(), username, context)
                    showDialog = false
                },
                selectedImage = selectedImage,
                onSelectedImageChange = { newSelectedImage -> selectedImage = newSelectedImage },
                username = username ?: "DefaultUsername",
                onUsernameChange = { newUsername -> username = newUsername },
                context = context
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    selectedImage: String?,
    onSelectedImageChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    context: Context
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
            Box(modifier = Modifier.clickable(onClick = { })) {
                ProfilePicture(selectedImage, context, true) { newSelectedImage ->
                    onSelectedImageChange(newSelectedImage)
                }
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
    MyLightTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(MaterialTheme.colorScheme.onBackground) // Color de fondo
        ) {
            Image(painter = painterResource(id = R.drawable.logo_letras), contentDescription = "Logo", modifier = Modifier
                .size(150.dp)
                .padding(start = 10.dp))

            // Icono de ajustes
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = "Settings",
                modifier = Modifier
                    .clickable { navController.navigate(AppScreens.SettingsScreen.route) }
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 16.dp) // Margen desde el borde superior y derecho
                    .size(40.dp) // Tamaño del icono
                    ,
                tint = Color.Black
            )
        }
    }

}

@Composable
fun ProfilePicture(
    imageUrl: String?,
    context: Context,
    isEditing: Boolean,
    onImageSelected: (String) -> Unit
) {

    val pickImageContract =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Aquí puedes manejar el Uri de la imagen seleccionada
            // Por ejemplo, puedes convertirlo a una cadena y asignarlo a selectedImage
            uri?.let {
                onImageSelected(it.toString())
            }
        }

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
            .clickable {
                Toast
                    .makeText(context, "Imagen clickeada", Toast.LENGTH_SHORT)
                    .show()
            }
            .clickable {
                if (isEditing) {
                    pickImageContract.launch("image/*")
                }
            },
        contentScale = ContentScale.Crop,

        )
}

@Composable
fun ProfileButton(text: String, icon: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        modifier = Modifier.fillMaxWidth().height(60.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = icon), contentDescription = "profile pic")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White)
        }
    }
}




