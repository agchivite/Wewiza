package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dam.wewiza_front.R
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

            MyListsScreenBodyContent(myListScreenViewModel, navController, showDialog)
        }
    }
}

@Composable
fun MyListsScreenBodyContent(
    viewModel: MyListsScreenViewModel,
    navController: NavHostController,
    showDialog: MutableState<Boolean>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 70.dp)
    ) {
        Column {
            val myLists by remember { viewModel.myLists }

            LazyColumn {
                items(myLists.size) { index ->
                    MyListItem(
                        myLists[index], viewModel, navController
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyListItem(
    shoppingList: ShoppingList,
    viewModel: MyListsScreenViewModel,
    navController: NavHostController
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(60.dp),
        onClick = {

            viewModel.navigateToListScreen(navController, shoppingList.uuid)
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = shoppingList.name)
            }


            // Delete button
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.End) {
                Button(
                    onClick = {
                        viewModel.deleteList(shoppingList.uuid)
                        viewModel.updateUserList()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete"
                    )
                }

            }

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
