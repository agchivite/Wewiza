package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.ShoppingList
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.MyListsScreenViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListsScreen(myListScreenViewModel: MyListsScreenViewModel, navController: NavHostController) {
    MyLightTheme() {
        Scaffold(
            bottomBar = {
                Constants.BottomMenu(navController)
            }
        ) {
            var showDialog = mutableStateOf(false)
            val selectedItemUuid = remember { mutableStateOf<String?>("") }

            MyListsScreenBodyContent(myListScreenViewModel, navController, showDialog, selectedItemUuid)
        }
    }
}

@Composable
fun MyListsScreenBodyContent(
    viewModel: MyListsScreenViewModel,
    navController: NavHostController,
    showDialog: MutableState<Boolean>,
    selectedItemUuid: MutableState<String?>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 70.dp)
    ) {
        Column {
            val myLists by remember { viewModel.myLists }

            LazyColumn {
                items(myLists) { shoppingList ->
                    MyListItem(
                        shoppingList = shoppingList,
                        viewModel = viewModel,
                        navController = navController,
                        showPopUp = selectedItemUuid.value == shoppingList.uuid,
                        onLongPressItem = { selectedUuid ->
                            selectedItemUuid.value = selectedUuid
                            Log.d("onLongPress", selectedUuid)
                            Log.d("onLongPress",  selectedItemUuid.value.toString())
                        },
                        onDismissRequest = { selectedUuid ->
                            if (selectedItemUuid.value == selectedUuid) {
                                selectedItemUuid.value = null
                                Log.d("onDimiss", selectedUuid)
                                Log.d("onDimiss",  selectedItemUuid.value.toString())
                            }
                        }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(), contentAlignment = Alignment.BottomEnd
            ) {
                AddNewListButton(showDialog, viewModel)
            }
        }
    }
}

@Composable
private fun MyListItem(
    shoppingList: ShoppingList,
    viewModel: MyListsScreenViewModel,
    navController: NavHostController,
    showPopUp: Boolean,
    onLongPressItem: (String) -> Unit,
    onDismissRequest: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(60.dp)
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPressItem(shoppingList.uuid)
                    },
                    onPress = {
                        viewModel.navigateToListScreen(navController, shoppingList.uuid)
                    }
                )
            },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = shoppingList.name)
        }
    }

    if (showPopUp) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = {
                onDismissRequest(shoppingList.uuid)
            }
        ) {
            DropdownMenuItem(
                text = { Text("Eliminar ${shoppingList.name}") },
                onClick = {
                    onDismissRequest(shoppingList.uuid)
                    viewModel.deleteList(shoppingList.uuid)
                    viewModel.updateUserList()
                }
            )
        }
    }
}

@Composable
fun AddNewListButton(
    showDialog: MutableState<Boolean>,
    viewModel: MyListsScreenViewModel
) {
    Box(contentAlignment = Alignment.Center) {
        ExtendedFloatingActionButton(
            onClick = { showDialog.value = true },
            icon = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            modifier = Modifier
                .size(130.dp)
                .padding(30.dp),
            text = { Text("Nueva Lista") }
        )
    }

    if (showDialog.value) {
        AddNewListDialog(showDialog, viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewListDialog(showDialog: MutableState<Boolean>, viewModel: MyListsScreenViewModel) {
    if (showDialog.value) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Nombre de la lista") },
            text = {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    label = { Text("Introduce el nombre de la lista") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {

                        viewModel.createNewList(textState.value.text, context)
                        viewModel.updateUserList()
                        showDialog.value = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Aquí puedes manejar el evento de clic en el botón de cancelar
                        showDialog.value = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
