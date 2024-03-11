package dev.sbytmacke.firstcompose.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.sbytmacke.firstcompose.screens.login.components.*
import dev.sbytmacke.firstcompose.viewmodels.LoginScreenViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginScreenViewModel
) {

    var emailText by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                NormalTextComponent(value = "Hey, there")
                HeadingTextComponent(value = "Welcome Back")
            }
            Spacer(modifier = Modifier.height(25.dp))
            Column {
                MyTextFieldComponent(
                    emailText,
                    onTextValueChanged = { newTextValue ->
                        emailText = newTextValue
                    },
                    labelValue = "Email",
                    icon = Icons.Outlined.Email
                )
                Spacer(modifier = Modifier.height(10.dp))
                PasswordTextFieldComponent(
                    password,
                    onTextValueChanged = { newTextValue ->
                        password = newTextValue
                    },
                    labelValue = "Password",
                    icon = Icons.Outlined.Lock
                )
            }
            BottomComponent(
                emailText,
                password,
                textQuery = "Don't have an account? ",
                textClickable = "Register",
                action = "Login",
                navController,
                viewModel
            )
        }
    }
}
