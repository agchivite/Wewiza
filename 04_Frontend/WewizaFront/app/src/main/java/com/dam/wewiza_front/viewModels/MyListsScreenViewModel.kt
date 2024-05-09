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
import java.util.UUID

class MyListsScreenViewModel: ViewModel() {

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
        if (text.isNotEmpty()) {
            val newList = myLists.value.toMutableList()
            newList.add(ShoppingList(uuid= UUID.randomUUID().toString(), products = mutableListOf(), name = text))
            myLists.value = newList
        }else{
            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
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
                        db.collection("profiles").document(auth.currentUser!!.email.toString()).set(profile!!)
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
            if (sharedViewModel.getLocalShoppingList().value == null){
                myLists.value.clear()
                val newList = profile!!.shoppingListsList
                sharedViewModel.setLocalShoppingList(newList)
                Log.d("MyListsScreenViewModel", "setLists: ${newList!!}")
                myLists.value = newList.toMutableList()
            }else {
                val newList = sharedViewModel.getLocalShoppingList().value
                myLists.value = newList!!.toMutableList()
            }

        }
    }

    fun navigateToListScreen(navController: NavHostController, uuid: String) {
        val selectedList = myLists.value.find { it.uuid == uuid }
        sharedViewModel.setSelectedList(selectedList!!)
        navController.navigate( AppScreens.ListScreen.route)
    }


}