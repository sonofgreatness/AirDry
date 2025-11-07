package com.example.airdry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.airdry.auth.SessionManager
import com.example.airdry.ui.nav.Navigation
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.screens.auth.LoginScreen
import com.example.airdry.ui.theme.AirDryTheme
import com.example.airdry.ui.theme.ThemeManager
import com.example.airdry.ui.viewmodels.AuthViewModel
import com.example.airdry.ui.viewmodels.ConnectionsViewModel
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.viewmodels.ProfileViewModel
import com.example.airdry.ui.viewmodels.SettingsViewModel
import com.example.airdry.ui.viewmodels.StackViewModel
import com.example.airdry.ui.viewmodels.WorkViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mEmployeeViewModel: EmployeeViewModel by viewModels()
    private val mWorkViewModel: WorkViewModel by viewModels()
    private val mConnectionsViewModel : ConnectionsViewModel by viewModels()
    private  val mAuthViewModel : AuthViewModel by viewModels()
    private  val mProfileViewModel: ProfileViewModel by viewModels()
    private  val mStackViewModel: StackViewModel by viewModels()
    private val mSettingsViewModel: SettingsViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var  connectivityObserver: ConnectivityObserver
    @Inject
    lateinit var themeManager: ThemeManager


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

                val isServerUp by  connectivityObserver.isServerReachable.collectAsState()
                val isUserLoggedIn  by sessionManager.isLoggedInFlow.collectAsState()
                val bool = shouldMoveToLoginScreen(isUserLoggedIn, isServerUp)
                AppContent(bool)

        }
    }
    private fun shouldMoveToLoginScreen(isUserLoggedIn: Boolean, isServerUp: Boolean): Boolean{

        println("isServerUp $isServerUp , isUserLoggedIn $isUserLoggedIn")
        return isServerUp && !isUserLoggedIn
    }
    @Composable
    fun AppContent(shouldMoveToLoginScreen: Boolean) {
        val  isDarkThemeEnabled by themeManager.isDarkThemeEnabled.collectAsState(initial = false)
        if (shouldMoveToLoginScreen == true) {
            val navController = rememberNavController()
            AirDryTheme(isDarkThemeEnabled) {LoginScreen(mAuthViewModel) { navController.navigate(Screen.SelectEmployee.route) } }

        } else {
            AirDryTheme(isDarkThemeEnabled) {
            Navigation(mEmployeeViewModel, mWorkViewModel,
                mConnectionsViewModel,
                mAuthViewModel,
                sessionManager,
                connectivityObserver,
                mProfileViewModel,
                stackViewModel=mStackViewModel,
                mSettingsViewModel,
                themeManager)
        }
        }
    }
}