@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.CustomerSupportScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomerSupportScreen(
    viewModel: CustomerSupportScreenViewModel,
    navController: NavHostController
) {

    Scaffold(
        topBar = {
            Constants.TopBarWithLogo(navController)
        }
    ) {
        MyLightTheme {
            CustomerSupportBodyContent(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSupportBodyContent(
    viewModel: CustomerSupportScreenViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(90.dp))

        Text(
            text = "¿Cómo podemos ayudarte?",
            fontFamily = FirsNeue,
            fontSize = 32.sp,
            modifier = Modifier.padding(start = 20.dp),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(30.dp))

        val userMsg = remember { mutableStateOf(TextFieldValue()) }

        TextField(
            value = userMsg.value,
            onValueChange = { userMsg.value = it },
            label = { Text("Escribe tu mensaje aquí") },
            modifier = Modifier
                .padding(16.dp)
                .height(400.dp)
                .width(350.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(30.dp))

        SendButton(viewModel, userMsg, context)
    }
}

@Composable
fun SendButton(viewModel: CustomerSupportScreenViewModel, userMsg: MutableState<TextFieldValue>, context: Context) {
    Button(onClick = {
        viewModel.sendMessage(userMsg.value.text, context)
        userMsg.value = TextFieldValue("")  // Clear the text field after sending the message
    }) {
        Row(
            modifier = Modifier
                .size(270.dp, 40.dp)
                .align(Alignment.CenterVertically), // This will center the items vertically
            horizontalArrangement = Arrangement.Center, // This will center the items horizontally
            verticalAlignment = Alignment.CenterVertically // This will center the items vertically
        ) {
            Text(
                text = "Enviar",
                modifier = Modifier.weight(1f),
                fontSize = 20.sp,
                fontFamily = FirsNeue
            )
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
