package com.dam.wewiza_front.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.navigation.AppScreens
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch

class ProfileScreenViewModel() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var user: FirebaseUser

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser != null) {
            //every time an user authenticates, we load the profile data, so we can assure that the data is always updated and it not the data of the previous user
            loadProfileData()
        }
    }

    var isLoading = mutableStateOf(true)
    var profile = mutableStateOf<Profile?>(null)

    init {
        auth.addAuthStateListener(authStateListener)
        loadProfileData()
    }

    private fun loadProfileData() {
        user = auth.currentUser!!
        viewModelScope.launch {
            isLoading.value = true
            recoverProfileData(user)
            isLoading.value = false
        }
    }

    /**
     * Method that recovers data from the profile of the user that is currently logged in
     */
    private fun recoverProfileData(user: FirebaseUser) {

        db.collection("profiles").document(user.email.toString()).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val loadedProfile = document.toObject(Profile::class.java)
                    Log.d("Profile", "Profile data recovered")
                    profile.value = loadedProfile
                } else {
                    Log.d("Profile", "Profile data not found")
                }
            }
    }

    fun navigateToMyListScreen(navController: NavController) {
        navController.navigate(AppScreens.MyListScreen.route)
    }

    fun navigateToCustomerSupportScreen(navController: NavController) {
        navController.navigate(AppScreens.CustomerSupportScreen.route)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener) //With this part of the code we remove the listener when the ViewModel is destroyed
    }

    fun pickImageFromGallery() {

    }

    fun updateProfileDataOnFiresbase(selectedImageUri: Uri?, username: String?, context: Context) {
        val user = auth.currentUser
        if (user != null) {
            val profileRef = db.collection("profiles").document(user.email.toString())
            val profileUpdates = hashMapOf<String, Any>()
            if (username != null) {
                profileUpdates["name"] = username
            } 

            // Si se seleccionó una imagen, subirla a Firebase Storage
            if (selectedImageUri != null) {
                val storageRef = Firebase.storage.reference
                val imageRef = storageRef.child("images/${user.uid}")
                val uploadTask = imageRef.putFile(selectedImageUri)

                uploadTask.addOnSuccessListener {
                    // Cuando la imagen se ha subido correctamente, obtener la URL de descarga
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Guardar la URL de descarga en Firestore
                        profileUpdates["imageUrl"] = downloadUri.toString()
                        updateProfileInFirestore(profileRef, profileUpdates, context)
                    }
                }.addOnFailureListener { e ->
                    Log.w("Profile", "Error uploading image", e)
                    // Aquí puedes manejar el error de la subida de la imagen
                }
            } else {
                // Si no se seleccionó una imagen, simplemente actualizar el perfil en Firestore
                updateProfileInFirestore(profileRef, profileUpdates, context)
            }
        }
    }


    private fun updateProfileInFirestore(profileRef: DocumentReference, profileUpdates: HashMap<String, Any>, context: Context) {
        profileRef.update(profileUpdates)
            .addOnSuccessListener {
                Log.d("Profile", "Profile updated")
                Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_LONG).show()
                loadProfileData()
            }
            .addOnFailureListener { e ->
                Log.w("Profile", "Error updating profile", e)
                // Aquí puedes manejar el error de la actualización del perfil
            }
    }
}






