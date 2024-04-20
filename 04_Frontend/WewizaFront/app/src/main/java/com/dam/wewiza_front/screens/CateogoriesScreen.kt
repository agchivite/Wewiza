@file:OptIn(ExperimentalMaterial3Api::class)

package com.dam.wewiza_front.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam.wewiza_front.constants.Constants
import com.dam.wewiza_front.models.Category
import com.dam.wewiza_front.ui.theme.MyLightTheme
import com.dam.wewiza_front.viewModels.CategoriesScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CategoriesScreen(
    viewModel: CategoriesScreenViewModel,
    navController: NavController
) {
    var searchText by remember { mutableStateOf("") }
    Scaffold(

        bottomBar = {
            Constants.BottomMenu(navController)
        }
    ) {
        MyLightTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
            ) {
                SearchBar(searchText, onValueChange = { searchText = it })
                CategoriesScreenBodyContent(viewModel, navController, searchText)
            }

        }
    }
}

@Composable
fun CategoriesScreenBodyContent(
    viewModel: CategoriesScreenViewModel,
    navController: NavController,
    searchText: String
) {


    val allCategories = viewModel.allCategoriesList
    val filteredCategories =
        allCategories.filter { it.name.contains(searchText, ignoreCase = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        content = {
            items(filteredCategories.size) { index ->
                CategoryItem(category = filteredCategories[index])
            }
        }
    )
}


@Composable
fun SearchBar(searchText: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = searchText,
            onValueChange = onValueChange,
            label = { Text("Buscar categoría") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
fun CategoryItem(category: Category) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = category.name,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}