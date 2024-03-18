package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputService
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dam.wewiza_front.R
import com.google.android.gms.maps.model.Circle


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

            TextInputs()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputs(){
    var textValue by remember { mutableStateOf(TextFieldValue()) }
    var passwordValue by remember { mutableStateOf(TextFieldValue()) }

    Column {
        TextField(
            value = textValue,
            onValueChange = { textValue = it },
            label = { Text("Nombre") },
            modifier = Modifier.padding(16.dp)
        )
        TextField(
            value = passwordValue,
            onValueChange = { passwordValue = it },
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