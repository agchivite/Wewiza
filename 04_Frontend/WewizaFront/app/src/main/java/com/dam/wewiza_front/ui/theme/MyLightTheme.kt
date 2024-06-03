package com.dam.wewiza_front.ui.theme


import androidx.compose.foundation.background
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyLightTheme(
    content: @Composable () -> Unit
){

    fun colors(): ColorScheme {
        return lightColorScheme(
            surface = Color(0xFFF2F2F2), // light grey
            onSurface = Color(0xFF2E2C31),
            primary = Color(0xFF8538A6), // light purple
            background = Color(0xFF7386C0),
            onBackground = Color(0xFF7386BF), // light blue
            tertiary = Color(0xFFF2CB57), // light yellow,
            onTertiary = Color(0xFF490459), // dark purple
        )
    }


    MaterialTheme(
        content = content,
        colorScheme = colors()
    )



}