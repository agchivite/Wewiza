package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.graphics.Paint.Style
import android.graphics.fonts.FontStyle
import android.icu.lang.UCharacter.VerticalOrientation
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dam.wewiza_front.R
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(){


    Scaffold() {
        BodyContent()
    }

}

@Composable
fun BodyContent(){
    var emailValue by remember { mutableStateOf(TextFieldValue()) }
    var passwordValue by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current

    Box(modifier = Modifier.padding(top = 20.dp)){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(id = R.drawable.provisional_logo),
                contentDescription = "Logo", modifier = Modifier.size(200.dp)
            )
            Text("¡Inicia Sesión en WeWiza!", style = TextStyle(fontSize = 30.sp))

            TextInputs(emailValue, passwordValue) { email, password ->
                emailValue = email
                passwordValue = password
            }
            Row() {
                Button(
                    onClick = {
                        val email = emailValue.text
                        val password = passwordValue.text
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Login Correcto", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Error al autenticar al usuario
                                    // Maneja el error según tus necesidades
                                }
                            }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Iniciar Sesión")
                }

                Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(16.dp)) {
                    Row(

                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painter = painterResource(id = R.drawable.google), contentDescription = "google", modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.size(20.dp))
                        Text(text = "Google")
                    }

                }
            }


        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputs(
    emailValue: TextFieldValue,
    passwordValue: TextFieldValue,
    onValuesChange: (email: TextFieldValue, password: TextFieldValue) -> Unit
) {
    Column {
        TextField(
            value = emailValue,
            onValueChange = { onValuesChange(it, passwordValue) },
            label = { Text("Correo") },
            modifier = Modifier.padding(16.dp)
        )
        TextField(
            value = passwordValue,
            onValueChange = { onValuesChange(emailValue, it) },
            label = { Text("Contraseña") },
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    LoginScreen()
}