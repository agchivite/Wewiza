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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileScreenViewModel() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var user: FirebaseUser
    private val sharedViewModel = SharedViewModel.instance

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
        viewModelScope.launch(Dispatchers.IO) {
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
                    sharedViewModel.setLocalProfile(loadedProfile)
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


    fun updateProfileDataOnFiresbase(
        originalImageUri: Uri?,
        selectedImageUri: Uri?,
        username: String?,
        context: Context
    ) {
        val user = auth.currentUser
        if (user != null) {
            val profileRef = db.collection("profiles").document(user.email.toString())
            val profileUpdates = hashMapOf<String, Any>()

            if (username != null) {
                profileUpdates["name"] = username
            }

            if (selectedImageUri != null && selectedImageUri != originalImageUri) {
                val storageRef = Firebase.storage.reference
                val imageRef = storageRef.child("images/${user.email}")

                val uploadTask = imageRef.putFile(selectedImageUri)
                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        profileUpdates["imageUrl"] = downloadUri.toString()
                        Log.d("Profile", "Image uploaded successfully")
                        updateProfileInFirestore(profileRef, profileUpdates, context)
                    }.addOnFailureListener { e ->
                        Log.w("Profile", "Error obtaining download URL", e)
                        Toast.makeText(
                            context,
                            "Error al obtener URL de descarga",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }.addOnFailureListener { e ->
                    Log.w("Profile", "Error uploading image", e)
                    Toast.makeText(context, "Error al subir la imagen", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.d("Profile", "No new image selected or image unchanged")
                updateProfileInFirestore(profileRef, profileUpdates, context)
            }
        }
    }

    private fun updateProfileInFirestore(
        profileRef: DocumentReference,
        profileUpdates: HashMap<String, Any>,
        context: Context
    ) {
        profileRef.update(profileUpdates)
            .addOnSuccessListener {
                Log.d("Profile", "Profile updated successfully")
                Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_LONG)
                    .show()
                loadProfileData()
            }
            .addOnFailureListener { e ->
                Log.w("Profile", "Error updating profile", e)
                Toast.makeText(context, "Error al actualizar el perfil", Toast.LENGTH_LONG).show()
            }
    }

}





