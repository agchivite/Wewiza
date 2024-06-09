package com.dam.wewiza_front

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.dam.wewiza_front.koinModules.appModule
import com.dam.wewiza_front.navigation.AppNavigation
import com.dam.wewiza_front.screens.MaintenanceScreen
import com.dam.wewiza_front.services.RetrofitServiceFactory
import com.dam.wewiza_front.ui.theme.WewizaFrontTheme
import com.dam.wewiza_front.viewModels.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

var profileViewModel: MutableState<ProfileScreenViewModel?> = mutableStateOf(null)

class MainActivity : ComponentActivity(), KoinComponent {
    val isAuthenticated = MutableLiveData<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val service = RetrofitServiceFactory.makeRetrofitService()
        val isConnectionError = mutableStateOf(false)
        try {
            lifecycleScope.launch {
                val result = safeApiCall { service.testConnection() }
                isConnectionError.value = result.isFailure

                Log.d("CONNECTION ERROR", "isConnectionError: ${isConnectionError.value}") // TRUE

                if (isConnectionError.value) {
                    setContent {
                        MaintenanceScreen()
                    }
                } else {
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
                    settingsViewModel.isUserSignedOut.observe(this@MainActivity) { isSignOut ->
                        if (isSignOut) {
                            profileViewModel.value = null
                        }
                    }

                    // Observe authentication state
                    isAuthenticated.value = auth.currentUser != null
                    auth.addAuthStateListener {
                        isAuthenticated.value = it.currentUser != null
                        Log.d("main", "isAuthenticated: ${isAuthenticated.value}")
                    }

                    isAuthenticated.observe(this@MainActivity) { isLoggedIn ->
                        profileViewModel.value = if (isLoggedIn) get() else null
                        Log.d("main", "ProfileViewModel: $profileViewModel")
                    }

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
                                    settingsViewModel,
                                    myListsScreenViewModel,
                                    customerSupportViewModel,
                                    productScreenViewModel,
                                    productDetailsScreenViewModel,
                                    listScreenViewModel,
                                    this@MainActivity
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
