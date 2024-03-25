package com.dam.wewiza_front.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.dam.wewiza_front.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegisterScreenViewModel: ViewModel() {

    val auth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _repeatPassword = MutableStateFlow("")
    val repeatPassword: StateFlow<String> = _repeatPassword

    fun setEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun setPassword(newPassword: String) {
        _password.value = newPassword
    }

    fun setRepeatPassword(newRepeatPassword: String){
        _repeatPassword.value = newRepeatPassword
    }


    fun registerWithMail(context: Context, navController: NavController) {
        val actualEmail = email.value
        val actualPassword = password.value
        val actualRepeatPassword = repeatPassword.value

        if (actualEmail.isNotEmpty() && actualPassword.isNotEmpty() && actualRepeatPassword.isNotEmpty()) {
            if (actualPassword == actualRepeatPassword) {
                auth.createUserWithEmailAndPassword(actualEmail, actualPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.navigate(AppScreens.HomeScreen.route)
                        } else {
                            if (task.exception?.message?.contains("already in use") == true) {
                                // El correo ya está registrado
                                Toast.makeText(context, "Este correo ya está registrado. Por favor, inicie sesión.", Toast.LENGTH_LONG).show()
                            } else {
                                // Otro tipo de fallo
                                Log.e("RegisterWithEmail", "Registration Failed", task.exception)
                                Toast.makeText(context, "Error al registrar. Por favor, inténtalo de nuevo.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            } else {
                // Las contraseñas no coinciden
                Toast.makeText(context, "Las contraseñas no coinciden. Por favor, inténtalo de nuevo.", Toast.LENGTH_LONG).show()
            }
        } else {
            // Campos vacíos
            Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_LONG).show()
        }
    }

}