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
            surface = Color(0xFFD0C2DC),
            onSurface = Color(0xFF2E2C31),
            primary = Color(0xFFBB84E8),
            background = Color(0xFF7386C0)
        )
    }


    MaterialTheme(
        content = content,
        colorScheme = colors()
    )



}