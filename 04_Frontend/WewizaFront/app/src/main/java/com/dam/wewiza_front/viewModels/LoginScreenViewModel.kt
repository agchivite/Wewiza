package com.dam.wewiza_front.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.dam.wewiza_front.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginScreenViewModel: ViewModel() {

    val auth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun setEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun setPassword(newPassword: String) {
        _password.value = newPassword
    }
    fun loginWithEmailPassword(context: Context, navController: NavController) {
        val actualEmail = email.value
        val actualPassword = password.value

        if (actualEmail.isNotEmpty() && actualPassword.isNotEmpty()) {
            try {
                auth.signInWithEmailAndPassword(actualEmail, actualPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginWithMail", "LoginSuccessful")
                            navController.navigate(AppScreens.HomeScreen.route)
                        } else {
                            Log.e("LoginWithMail", "Login Failed", task.exception)
                            if (task.exception is FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(context, "Credenciales incorrectas.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }catch (e: Exception){
                e.printStackTrace()
            }

        } else {
            Toast.makeText(context, "Complete los campos.", Toast.LENGTH_LONG).show()
        }
    }

}