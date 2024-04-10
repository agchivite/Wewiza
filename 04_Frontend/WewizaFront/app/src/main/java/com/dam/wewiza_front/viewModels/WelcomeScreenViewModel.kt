package com.dam.wewiza_front.viewModels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Product
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.models.Store
import com.dam.wewiza_front.models.UsersGroceryList
import com.dam.wewiza_front.navigation.AppScreens
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.UUID

class WelcomeScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    val db = FirebaseFirestore.getInstance()


    fun signInWithGoogleCredential(credential: AuthCredential , navController: NavController) {
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            createUserInFirestore()

                            Log.i("Login", "Usuario logueado con google")
                            navController.navigate(route = AppScreens.HomeScreen.route)
                        }
                    }
                    .addOnFailureListener {
                        Log.i("Login", "error al loguear con google")
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createUserInFirestore() {
        val user = auth.currentUser
        val email = user!!.email

        // Comprobar si ya existe un perfil con el mismo uid
        db.collection("profiles").document(email!!).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("Firestore", "Profile already exists!")
                } else {
                    val profile = Profile(
                        email = email,
                        name = user.displayName ?: "DefaultUserName",
                        imageUrl = user.photoUrl?.toString() ?: "",
                        reviews = 0,
                        shoppingListsList = emptyList<ShoppingList>(),
                        recentSearches = emptyList<String>()
                    )
                    db.collection("profiles").document(email).set(profile)
                        .addOnSuccessListener { Log.d("Firestore", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("Firestore", "Error writing document", e) }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error checking document", e)
            }
    }

    fun loginWithGoogle(
        context: Context,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        ) {


        viewModelScope.launch{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.FIREBASE_CLIENT_ID)
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(context, gso)

            googleClient.signOut() //Cierra la sesion por si hubiera algun error, para iniciar con la cuenta deseada
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        launcher.launch(googleClient.signInIntent)
                    }
                }
        }
    }

    fun navigateToLoginScreen(navController: NavController) {
        navController.navigate(route = AppScreens.LoginScreen.route)
    }

    fun navigateToRegisterScreen(navController: NavController) {
        navController.navigate(route = AppScreens.RegisterScreen.route)
    }


    /**
     * NO deberia estar aqui esta funcion, la puse para una guarrada con el firebase
     */
    fun addToFirestore() {
        val db = FirebaseFirestore.getInstance()
        val groceryList = UsersGroceryList(
            name = "Mi lista de compras",
            products = listOf(
                Product(
                    UUID.randomUUID().toString(),
                    "verduras",
                    "",
                    "kg",
                    "ProductoPrueba",
                    2.50f,
                    14.50f,
                    2,
                    "storeUrl",
                    "MarketPrueba"
                )
            )
        )

        db.collection("lists")
            .add(groceryList)
            .addOnSuccessListener { documentReference ->
                println("Lista de compras agregada con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error al agregar la lista de compras: $e")
            }
    }


}