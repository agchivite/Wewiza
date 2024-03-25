package com.dam.wewiza_front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color.Companion.Green
import com.dam.wewiza_front.navigation.AppNavigation
import com.dam.wewiza_front.screens.WelcomeScreen
import com.dam.wewiza_front.ui.theme.WewizaFrontTheme
import com.dam.wewiza_front.viewModels.WelcomeScreenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WewizaFrontTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}
