package com.dam.wewiza_front.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.dam.wewiza_front.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SettingsScrennViewModel {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    fun deleteAccount(navController: NavController, context: Context) {
        val currentUser = auth.currentUser

        currentUser?.let { user ->
            try{
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.navigate(AppScreens.WelcomeScreen.route)
                            Toast.makeText(context, "La cuenta se ha eliminado correctamente", Toast.LENGTH_LONG).show()
                            deleteDataFromFirebase(currentUser)
                        } else {
                            Toast.makeText(context, "No se ha podido eliminar la cuenta", Toast.LENGTH_LONG).show()
                        }
                    }
            }catch(e: Exception){
                Log.e("Delete Account exception", e.message.toString())
            }

        }
    }

    private fun deleteDataFromFirebase(currentUser: FirebaseUser) {
        /*
        "Hay que hacer que se eliminen todos los elementos de la collecion de listas"
                " y datos del perfil, segun el id de este usuario. CurrentUser.uid"

         */

        val email = currentUser.email
        db.collection("profiles").document(email!!).delete()
            .addOnCompleteListener {
                Log.d("Delete account", "Profile data deleted")
            }


    }

    fun signOut(navController: NavController, context: Context) {
        auth.signOut()
        navController.navigate(AppScreens.WelcomeScreen.route)
        Toast.makeText(context, "Sesión cerrada correctamente", Toast.LENGTH_LONG).show()
    }

}