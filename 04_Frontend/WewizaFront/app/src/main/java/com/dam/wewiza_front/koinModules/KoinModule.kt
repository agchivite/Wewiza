package com.dam.wewiza_front.koinModules


import com.dam.wewiza_front.viewModels.AboutUsScreenViewModel
import com.dam.wewiza_front.viewModels.CategoriesScreenViewModel
import com.dam.wewiza_front.viewModels.CustomerSupportScreenViewModel
import com.dam.wewiza_front.viewModels.HomeScreenViewModel
import com.dam.wewiza_front.viewModels.ListScreenViewModel
import com.dam.wewiza_front.viewModels.LoginScreenViewModel
import com.dam.wewiza_front.viewModels.MyListsScreenViewModel
import com.dam.wewiza_front.viewModels.ProductDetailsScreenViewModel
import com.dam.wewiza_front.viewModels.ProductScreenViewModel
import com.dam.wewiza_front.viewModels.ProfileScreenViewModel
import com.dam.wewiza_front.viewModels.RegisterScreenViewModel
import com.dam.wewiza_front.viewModels.SettingsScrennViewModel
import com.dam.wewiza_front.viewModels.SharedViewModel
import com.dam.wewiza_front.viewModels.SuggestionScreenViewModel
import com.dam.wewiza_front.viewModels.WelcomeScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { WelcomeScreenViewModel() }
    viewModel { HomeScreenViewModel() }
    viewModel { LoginScreenViewModel() }
    viewModel { RegisterScreenViewModel() }
    viewModel { AboutUsScreenViewModel() }
    viewModel { SuggestionScreenViewModel() }
    viewModel { CategoriesScreenViewModel() }
    viewModel { ProfileScreenViewModel() }
    viewModel { SettingsScrennViewModel() }
    viewModel { MyListsScreenViewModel() }
    viewModel { CustomerSupportScreenViewModel() }
    viewModel { ProductScreenViewModel() }
    viewModel { ProductDetailsScreenViewModel() }
    viewModel { SharedViewModel()}
    viewModel {ListScreenViewModel()}
}