package com.dam.wewiza_front.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileScreenViewModel() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var isLoading = mutableStateOf(true)
    var profile = mutableStateOf<Profile?>(null)

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            isLoading.value = true
            recoverProfileData()
            isLoading.value = false
        }

    }

        /**
         * Method that recovers data from the profile of the user that is currently logged in
         */
        private suspend fun recoverProfileData(){
            val user = auth.currentUser
            val uid = user!!.uid

            db.collection("profiles").document(uid).get().addOnSuccessListener { document ->
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
    }




