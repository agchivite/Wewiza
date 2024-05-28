package com.dam.wewiza_front.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.MainActivity
import com.dam.wewiza_front.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsScrennViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val isUserSignedOut = MutableLiveData<Boolean>()
    private val sharedViewModel = SharedViewModel.instance

    fun deleteAccount(navController: NavController, context: Context, mainActivity: MainActivity) {
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    user.delete().await()
                    deleteDataFromFirebase(user)

                    launch(Dispatchers.Main) {
                        navController.navigate(AppScreens.WelcomeScreen.route)
                        isUserSignedOut.value = true
                        Toast.makeText(context, "La cuenta se ha eliminado correctamente", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("Delete Account exception", e.message.toString())
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "No se ha podido eliminar la cuenta", Toast.LENGTH_LONG).show()
                        Toast.makeText(context, "Reinicia la aplicación e inténtalo de nuevo", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private suspend fun deleteDataFromFirebase(currentUser: FirebaseUser) {
        val email = currentUser.email
        try {
            email?.let {
                // Elimina el documento del perfil del usuario
                db.collection("profiles").document(it).delete().await()
                // Elimina todas las listas de compras asociadas al usuario
                val lists = db.collection("shoppingLists").whereEqualTo("userId", currentUser.uid).get().await()
                for (list in lists) {
                    db.collection("shoppingLists").document(list.id).delete().await()
                }
                Log.d("Delete account", "Profile data and lists deleted")
            }
        } catch (e: Exception) {
            Log.e("Delete Data exception", e.message.toString())
        }
    }

    fun signOut(navController: NavController, context: Context, mainActivity: MainActivity) {
        auth.signOut()
        sharedViewModel.resetLocalData()
        navController.navigate(AppScreens.WelcomeScreen.route)
        Toast.makeText(context, "Sesión cerrada correctamente", Toast.LENGTH_LONG).show()
        isUserSignedOut.value = true
    }
}
