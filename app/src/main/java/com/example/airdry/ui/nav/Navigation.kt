package com.example.airdry.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.auth.SessionManager
import com.example.airdry.ui.AirDryTopAppBar
import com.example.airdry.ui.screens.auth.LoginScreen
import com.example.airdry.ui.screens.auth.ProfileScreen
import com.example.airdry.ui.screens.protection.ProductKeyScreen
import com.example.airdry.ui.screens.settings.AboutScreen
import com.example.airdry.ui.screens.settings.EditStackScreen
import com.example.airdry.ui.screens.settings.ManageStacksScreen
import com.example.airdry.ui.screens.settings.ManageUpdatesScreen
import com.example.airdry.ui.screens.settings.Settings
import com.example.airdry.ui.screens.work.EditWorkScreen
import com.example.airdry.ui.screens.work.RecordWorkScreen
import com.example.airdry.ui.screens.work.WorkListScreen
import com.example.airdry.ui.screens.workers.CreateWorkerScreen
import com.example.airdry.ui.screens.workers.EditWorkerScreen
import com.example.airdry.ui.screens.workers.WorkersListScreen
import com.example.airdry.ui.theme.ThemeManager
import com.example.airdry.ui.viewmodels.AuthViewModel
import com.example.airdry.ui.viewmodels.ConnectionsViewModel
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.viewmodels.ProfileViewModel
import com.example.airdry.ui.viewmodels.RecordWorkViewModel
import com.example.airdry.ui.viewmodels.SettingsViewModel
import com.example.airdry.ui.viewmodels.StackViewModel
import com.example.airdry.ui.viewmodels.WorkViewModel
import com.example.airdry.util.UtilityFunctions.Companion.toUUID



@Composable
fun Navigation(
    employeeViewModel: EmployeeViewModel,
    workViewModel: WorkViewModel,
    connectionsViewModel: ConnectionsViewModel,
    authViewModel: AuthViewModel,
    sessionManager: SessionManager,
    myConnectivityObserver: MyConnectivityObserver,
    profileViewModel: ProfileViewModel,
    stackViewModel: StackViewModel,
    settingsViewModel: SettingsViewModel,
    recordWorkViewModel: RecordWorkViewModel,
    themeManager: ThemeManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = "BrotherHood Mobile"
    val showBackIcon = currentRoute !in listOf(
        Screen.Workers.route,
        Screen.SelectEmployee.route,
        Screen.Settings.route,
        Screen.Login.route
    )

    val isServerUp by myConnectivityObserver.isServerReachable.collectAsState()
    val isUserLoggedIn by sessionManager.isLoggedInFlow.collectAsState()
    val darkTheme by themeManager.isDarkThemeEnabled.collectAsState()

    Scaffold(
        topBar = {
            AirDryTopAppBar(
                title = title,
                showBackIcon = showBackIcon,
                onBackClick = { navController.popBackStack() },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                isServerUp = isServerUp,
                isUserLoggedIn = isUserLoggedIn
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(Modifier.padding(padding)) {

            NavHost(
                navController = navController,
                startDestination = Screen.SelectEmployee.route
            ) {

                composable(Screen.SelectEmployee.route) {
                    WorkListScreen(workViewModel, navController)
                }

                composable(Screen.Workers.route) {
                    WorkersListScreen(navController, employeeViewModel)
                }

                composable(Screen.Settings.route) {
                    Settings(
                        navController,
                        settingsViewModel,
                        darkThemeEnabled = darkTheme,
                        onToggleTheme = { themeManager.setDarkTheme(it) },
                        myConnectivityObserver
                    )
                }

                composable(Screen.CreateWorker.route) {
                    CreateWorkerScreen(navController, employeeViewModel)
                }

                composable(Screen.RecordWork.route) {
                    RecordWorkScreen(recordWorkViewModel, navController)
                }

                composable(Screen.ManageUpdates.route) {
                    ManageUpdatesScreen(connectionsViewModel)
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(profileViewModel,navController, sessionManager)
                }

                composable(
                    route = Screen.EditWorker.route + "/{workerId}",
                    arguments = listOf(navArgument("workerId") { type = NavType.StringType })
                ) { entry ->
                    EditWorkerScreen(
                        navController,
                        entry.arguments?.getString("workerId").toString(),
                        employeeViewModel
                    )
                }

                composable(
                    route = Screen.EditWork.route + "/{workId}",
                    arguments = listOf(navArgument("workId") { type = NavType.StringType })
                ) { entry ->
                    val workId = entry.arguments?.getString("workId")!!
                    EditWorkScreen(
                        recordWorkViewModel,
                        workId.toUUID(),
                        navController,

                    )
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(Screen.Workers, Screen.SelectEmployee, Screen.Settings)
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    Box {
        NavigationBar {
            screens.forEachIndexed { index, screen ->
                if (index == 1) {
                    Spacer(Modifier.weight(1f))
                } else {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon!!,
                                contentDescription = screen.title,
                                tint = if (currentDestination?.route == screen.route)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.route == screen.route,
                        onClick = { navController.navigateTopLevel(screen.route) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigateTopLevel(Screen.SelectEmployee.route)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-36).dp)
                .size(64.dp),
            shape = CircleShape
        ) {
            Icon(Screen.WorkDone.icon!!, contentDescription = "Work Done")
        }
    }
}
