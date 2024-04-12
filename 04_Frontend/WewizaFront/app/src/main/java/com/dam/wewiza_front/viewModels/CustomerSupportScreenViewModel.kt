package com.dam.wewiza_front.viewModels

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel

class CustomerSupportScreenViewModel(): ViewModel() {
    fun sendMessage(userMsg: String, context: Context) {
        if (userMsg.isBlank()) {
            Toast.makeText(context, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("jiachengmadrid@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Mensaje de la aplicación")
                putExtra(Intent.EXTRA_TEXT, userMsg)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                startActivity(context, Intent.createChooser(intent, "Enviar correo..."), null)
            }
        }
    }
}