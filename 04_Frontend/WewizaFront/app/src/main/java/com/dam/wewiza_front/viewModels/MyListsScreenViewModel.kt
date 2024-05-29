package com.dam.wewiza_front.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.dam.wewiza_front.models.Profile
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MyListsScreenViewModel : ViewModel() {

    val myLists = mutableStateOf(mutableListOf<ShoppingList>())
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val sharedViewModel = SharedViewModel.instance
    private var profile: Profile? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            setProfileAndLists()
            clearData()
        }
    }


    private suspend fun clearData() {
        while (auth.currentUser != null) {
            delay(100)
        }
        myLists.value.clear()
        sharedViewModel.resetLocalData()
    }

    private suspend fun setProfileAndLists() {
        while (sharedViewModel.getLocalProfile().value == null) {
            delay(100) // Espera 100 milisegundos antes de verificar de nuevo
        }
        profile = sharedViewModel.getLocalProfile().value
        setLists()
    }

    fun createNewList(text: String, context: Context) {
        if (text.isNotEmpty() && !isListNameExists(text)) {
            val newList = myLists.value.toMutableList()
            newList.add(
                ShoppingList(
                    uuid = UUID.randomUUID().toString(),
                    products = mutableListOf(),
                    name = text
                )
            )
            myLists.value = newList
        } else if (isListNameExists(text)){
            Toast.makeText(context, "La lista $text ya existe", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isListNameExists(name: String): Boolean {
        return myLists.value.any { it.name == name }
    }

    fun deleteList(uuid: String) {
        val newList = myLists.value.filter { it.uuid != uuid }.toMutableList()
        myLists.value = newList
    }

    fun updateUserList() {
        if (profile == null) {
            profile = sharedViewModel.getLocalProfile().value
        }
        if (profile != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {

                    if (profile != null) {
                        profile!!.shoppingListsList = myLists.value
                        db.collection("profiles").document(auth.currentUser!!.email.toString())
                            .set(profile!!)
                    }

                    Log.d("UpdateUserReview", profile.toString())
                    Log.d("UpdateUserReview", "updateUserList: Lists updated")

                } catch (e: Exception) {
                    Log.d("UpdateUserReview", "updateUserList: ${e.message}")
                }
            }
        }
    }

    private fun setLists() {
        if (profile != null) {
            if (sharedViewModel.getLocalShoppingLists().value == null) {
                myLists.value.clear()
                val newList = profile!!.shoppingListsList
                sharedViewModel.setLocalShoppingList(newList)
                Log.d("MyListsScreenViewModel", "setLists: ${newList!!}")
                myLists.value = newList.toMutableList()
            } else {
                val newList = sharedViewModel.getLocalShoppingLists().value
                myLists.value = newList!!.toMutableList()
            }

        }
    }

    fun navigateToListScreen(navController: NavHostController, uuid: String) {
        viewModelScope.launch {
            retrieveShoppingListFromProfile(auth.currentUser!!.email!!, uuid) { selectedList ->
                selectedList?.let { // Verifica si la lista seleccionada no es nula
                    Log.d("MyListsScreenViewModel", "selectedList: $selectedList")
                    sharedViewModel.setSelectedList(selectedList)
                    navController.navigate(AppScreens.ListScreen.route)
                } ?: run {

                    Log.e("MyListsScreenViewModel", "Error: No se pudo recuperar la lista seleccionada")
                }
            }
        }
    }



    private fun retrieveShoppingListFromProfile(profileEmail: String, listUuid: String, callback: (ShoppingList?) -> Unit) {
        val profileRef = db.collection("profiles").document(profileEmail)
        var shoppingList: ShoppingList? = null

        profileRef.get().addOnSuccessListener { document ->
            val profile = document.toObject(Profile::class.java)

            if (profile?.shoppingListsList?.isNotEmpty() == true) {
                val wantedList = profile.shoppingListsList!!.find { it.uuid == listUuid }
                callback(wantedList)
            } else {
                callback(null)
            }
        }.addOnFailureListener { exception ->
            Log.e("RetrieveListFromFirebase", "Error retrieving profile", exception)
            callback(null)
        }
    }

    fun navigateToSuggestionScreen(navController: NavHostController) {
        navController.navigate(AppScreens.SuggestionScreen.route)
    }


}