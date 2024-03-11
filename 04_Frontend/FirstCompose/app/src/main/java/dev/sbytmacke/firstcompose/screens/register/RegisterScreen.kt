package dev.sbytmacke.firstcompose.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.sbytmacke.firstcompose.screens.login.components.*
import dev.sbytmacke.firstcompose.viewmodels.LoginScreenViewModel

@Composable
fun RegisterScreen(navController: NavHostController, loginUserViewModel: LoginScreenViewModel) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // TODO: checkbox

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
            NormalTextComponent(value = "Hello there,")
            HeadingTextComponent(value = "Create an Account")
            Spacer(modifier = Modifier.height(25.dp))

            Column {
                MyTextFieldComponent(
                    firstName,
                    onTextValueChanged = { newTextValue ->
                        firstName = newTextValue
                    },
                    labelValue = "First Name",
                    icon = Icons.Outlined.Person
                )
                Spacer(modifier = Modifier.height(10.dp))
                MyTextFieldComponent(
                    lastName,
                    onTextValueChanged = { newTextValue ->
                        lastName = newTextValue
                    },
                    labelValue = "Last Name",
                    icon = Icons.Outlined.Person
                )
                Spacer(modifier = Modifier.height(10.dp))
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
                CheckboxComponent()
                BottomComponent(
                    emailText,
                    password,
                    textQuery = "Already have an account? ",
                    textClickable = "Login",
                    action = "Register",
                    navController,
                    loginUserViewModel
                )
            }
        }
    }
}
