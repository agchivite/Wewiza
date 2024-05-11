package com.dam.wewiza_front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.dam.wewiza_front.koinModules.appModule
import com.dam.wewiza_front.navigation.AppNavigation
import com.dam.wewiza_front.ui.theme.WewizaFrontTheme
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
import com.dam.wewiza_front.viewModels.SuggestionScreenViewModel
import com.dam.wewiza_front.viewModels.WelcomeScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity(), KoinComponent {

    var profileViewModel: ProfileScreenViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        val auth = FirebaseAuth.getInstance()
        val welcomeViewModel = get<WelcomeScreenViewModel>()
        val homeViewModel = get<HomeScreenViewModel>()
        val loginViewModel = get<LoginScreenViewModel>()
        val registerViewModel = get<RegisterScreenViewModel>()
        val aboutUsViewModel = get<AboutUsScreenViewModel>()
        val suggestionViewModel = get<SuggestionScreenViewModel>()
        val categoriesViewModel = get<CategoriesScreenViewModel>()

        val settingsViewModel = get<SettingsScrennViewModel>()
        settingsViewModel.isUserSignedOut.observe(this) { isSignOut ->
            if (isSignOut) {
                profileViewModel = null
            }
        }
        profileViewModel = if (auth.currentUser != null) get() else null


        val productDetailsScreenViewModel = get<ProductDetailsScreenViewModel>()
        val myListsScreenViewModel = get<MyListsScreenViewModel>()


        val customerSupportViewModel = get<CustomerSupportScreenViewModel>()
        val productScreenViewModel = get<ProductScreenViewModel>()
        val listScreenViewModel = get<ListScreenViewModel>()

        setContent {
            WewizaFrontTheme {
                Surface {
                    // Pasar los viewmodels como argumentos a AppNavigation
                    AppNavigation(
                        welcomeViewModel,
                        homeViewModel,
                        loginViewModel,
                        registerViewModel,
                        aboutUsViewModel,
                        suggestionViewModel,
                        categoriesViewModel,
                        profileViewModel,
                        settingsViewModel,
                        myListsScreenViewModel,
                        customerSupportViewModel,
                        productScreenViewModel,
                        productDetailsScreenViewModel,
                        listScreenViewModel,
                        this
                    )
                }
            }
        }
    }


    /**
     * Removes the possibility of going back to the previous screen with the back button of the device
     */
    @Deprecated("deprecated")
    override fun onBackPressed() {
        val aux = false
        if (aux) {
            super.onBackPressed()
        }
    }

}
