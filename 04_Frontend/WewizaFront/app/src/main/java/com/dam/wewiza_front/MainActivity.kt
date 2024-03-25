package com.dam.wewiza_front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
<<<<<<< HEAD
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dam.wewiza_front.ui.theme.WewizaFrontTheme
=======
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color.Companion.Green
import com.dam.wewiza_front.navigation.AppNavigation
import com.dam.wewiza_front.screens.WelcomeScreen
import com.dam.wewiza_front.ui.theme.WewizaFrontTheme
import com.dam.wewiza_front.viewModels.WelcomeScreenViewModel
>>>>>>> dev

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WewizaFrontTheme {
<<<<<<< HEAD
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
=======
                Surface {
                    AppNavigation()
>>>>>>> dev
                }
            }
        }
    }
}
<<<<<<< HEAD

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WewizaFrontTheme {
        Greeting("Android")
    }
}
=======
>>>>>>> dev
