package com.dam.wewiza_front.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class RegisterScreenViewModel: ViewModel() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

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

        if (!isEmailValid(actualEmail)) {
            Toast.makeText(context, "Por favor, introduce un correo electrónico válido.", Toast.LENGTH_LONG).show()
            return
        }

        // Verificación de contraseña
        if (actualPassword.length < 6) {
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_LONG).show()
            return
        }


        if (actualEmail.isNotEmpty() && actualPassword.isNotEmpty() && actualRepeatPassword.isNotEmpty()) {
            if (actualPassword == actualRepeatPassword) {
                auth.createUserWithEmailAndPassword(actualEmail, actualPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            setUpFirestoreProfile(context)
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


    /*+
    * Función que crea un perfil en Firestore para el usuario que se acaba de registrar
    * se da por hecho que un usuario que tiene que registrarse no tiene perfil, por lo que no
    * se hace la comprobacion previa de si ya existe un perfil con el mismo uid
     */
    private fun setUpFirestoreProfile(context: Context) {
        val user = auth.currentUser
        val email = user!!.email

        val profile = Profile(
            email = email,
            name = user.email.toString().split("@")[0],
            imageUrl = user.photoUrl?.toString() ?: "",
            reviews = 0,
            shoppingListsList = emptyList<ShoppingList>(),
            recentSearches = emptyList<String>()
        )

        db.collection("profiles").document(email!!).set(profile)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing document", e)
                Toast.makeText(context, "Error al crear el perfil. Por favor, inténtalo de nuevo.", Toast.LENGTH_LONG).show()
            }
    }


    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,3}$"
        )
        return emailPattern.matcher(email).matches()
    }


}