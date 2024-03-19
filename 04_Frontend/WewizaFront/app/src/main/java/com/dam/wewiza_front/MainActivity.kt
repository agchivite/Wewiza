package com.dam.wewiza_front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.tooling.preview.Preview
import com.dam.wewiza_front.screens.LoginScreen
import com.dam.wewiza_front.ui.theme.WewizaFrontTheme
import com.dam.wewiza_front.viewModels.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WewizaFrontTheme {
                Surface(color = Green) {
                    LoginScreen(viewModel = LoginViewModel())
                }
            }
        }
    }
}
