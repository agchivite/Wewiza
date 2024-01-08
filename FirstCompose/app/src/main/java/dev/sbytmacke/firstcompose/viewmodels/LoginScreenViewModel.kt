package dev.sbytmacke.firstcompose.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dev.sbytmacke.firstcompose.routes.AppRoute

class LoginScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    // Opcional: que nos ayuda para evitar que se haga login varias veces
    private val _login = MutableLiveData(false)

    // Tiene que ser composable?? COMO?
    fun signInWithEmailAndPassword(email: String, password: String, navController: NavHostController): Boolean {
        val resultAuth = auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    Log.i("LoginScreenViewModel", "Bienvenido ${user.displayName}")
                    navController.navigate(AppRoute.HOME)
                } else {
                    Log.e("LoginScreenViewModel", "Email o contraseña incorrectos")
                }
            } else {
                Log.e("LoginScreenViewModel", "Email o contraseña incorrectos")
            }
            _login.value = task.isSuccessful
        }

        return (resultAuth.isComplete)
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }
}
