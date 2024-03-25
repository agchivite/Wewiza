package com.dam.wewiza_front.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
        RegisterScreenBodyContent(viewModel, navController)
    }
}

@Composable
fun RegisterScreenBodyContent(viewModel: RegisterScreenViewModel, navController: NavController) {

    val context = LocalContext.current

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

            RegisterTextInputs(viewModel)
            Spacer(modifier = Modifier.size(20.dp))
            RegisterScreenButtons(viewModel, navController, context)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTextInputs(viewModel: RegisterScreenViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val repeatPassword  by viewModel.repeatPassword.collectAsState()

    Column {
        TextField(
            value = email,
            onValueChange = { viewModel.setEmail(it)},
            label = { Text("Correo") },
            modifier = Modifier
                .padding(top = 30.dp)
                .width(350.dp)
        )
        TextField(
            value = password,
            onValueChange = { viewModel.setPassword(it) },
            label = { Text("Contraseña") },
            modifier = Modifier
                .padding(top = 20.dp)
                .width(350.dp)
        )

        TextField(
            value = repeatPassword,
            onValueChange = { viewModel.setRepeatPassword(it)},
            label = { Text("Repetir contraseña") },
            modifier = Modifier
                .padding(top = 20.dp)
                .width(350.dp)
        )
    }
}

@Composable
fun RegisterScreenButtons(
    viewModel: RegisterScreenViewModel,
    navController: NavController,
    context: Context
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {viewModel.registerWithMail(context, navController)},
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(start = 20.dp)
                .padding(end = 20.dp)
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBB84E8), // Color hexadecimal convertido a Color
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium


        ) {
            Text("Registrar", style = TextStyle(fontSize = 20.sp), fontFamily = FirsNeue)
        }

        Button(
            onClick = {navController.popBackStack()},
            modifier = Modifier
                .padding(start = 20.dp)
                .padding(end = 20.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth()
                .height(50.dp)
                .border(3.dp, Color(0xFFBB84E8), RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD0C2DC), contentColor = Color(0xFFBB84E8)
            ),
            shape = MaterialTheme.shapes.medium,

            ) {
            Row(

                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cancelar", style = TextStyle(fontSize = 20.sp), fontFamily = FirsNeue)
            }

        }
    }
}

