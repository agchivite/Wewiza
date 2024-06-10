package com.dam.wewiza_front.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerSupportScreenViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun sendMessage(userMsg: String, context: Context) {
        val user = auth.currentUser

        if (userMsg.isBlank()) {
            Toast.makeText(context, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
        } else if (user == null) {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        } else {
            val userEmail = user.email ?: "email no disponible"

            // Save message and email to Firestore
            val messageData = hashMapOf(
                "email" to userEmail,
                "message" to userMsg,
            )

            db.collection("support")
                .add(messageData)
                .addOnSuccessListener {
                    Toast.makeText(context, "¡Mensaje enviado!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error al guardar el mensaje: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}