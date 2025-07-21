package com.example.airdry.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.airdry.database.Entities.Employee
import com.example.airdry.database.Entities.Stack
import com.example.airdry.ui.screens.work.CreateWorkDoneScreen
import com.example.airdry.ui.screens.work.EmployeeSelectionScreen
import com.example.airdry.ui.screens.work.StackFormScreen
import com.example.airdry.ui.screens.workers.CreateWorkerScreen
import com.example.airdry.ui.screens.workers.EditWorkerScreen
import com.example.airdry.ui.screens.workers.WorkersListScreen
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.viewmodels.WorkViewModel
import java.util.UUID

@Composable
fun Navigation(employeeViewModel: EmployeeViewModel, workViewModel: WorkViewModel)  {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars),
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = navController, startDestination = Screen.WorkDone.route) {
                composable(Screen.WorkDone.route) {


                    Greeting("Work Done", navController = navController)
                }
                composable(Screen.Workers.route) {
                    WorkersListScreen(navController= navController, viewModel = employeeViewModel)
                }

                composable(Screen.CreateWorker.route) {
                    CreateWorkerScreen(navController= navController, viewModel = employeeViewModel)
                }

                composable(
                    route = Screen.EditWorker.route + "/{workerId}",
                    arguments = listOf(
                        navArgument("workerId") {
                            type = NavType.StringType
                        },
                    )
                ) { entry ->
                   val  id = entry.arguments?.getString("workerId")
                    EditWorkerScreen(navController= navController, employeeId = id.toString()
                        , viewModel = employeeViewModel)
                }
                composable(Screen.Settings.route) {
                    Greeting("Settings", navController = navController)
                }


                composable(Screen.SelectEmployee.route) {
                    EmployeeSelectionScreen(viewModel = workViewModel) {
                        val selectedId = workViewModel.selectedEmployee.value?.id
                        if (selectedId != null) {
                            navController.navigate(Screen.CreateStack.withArgs(selectedId.toString()))
                        }
                    }
                }
                composable(
                    route = Screen.CreateStack.route + "/{employeeId}",
                    arguments = listOf(navArgument("employeeId") { type = NavType.StringType })
                ) { backStackEntry ->

                    val employeeId = UUID.fromString(backStackEntry.arguments?.getString("employeeId"))
                    StackFormScreen(viewModel = workViewModel) { createdStack ->
                        val stackId = createdStack.id
                        navController.navigate(Screen.RecordWork.withArgs(employeeId.toString(), stackId.toString()))
                    }
                }

                composable(
                    route = Screen.RecordWork.route + "/{employeeId}/{stackId}",
                    arguments = listOf(
                        navArgument("employeeId") { type = NavType.StringType },
                        navArgument("stackId") { type = NavType.StringType }
                    )
                ) { entry ->
                    val employeeId = UUID.fromString(entry.arguments?.getString("employeeId")!!)
                    val stackId = UUID.fromString(entry.arguments?.getString("stackId")!!)

                    // State to hold the data
                    var employee by remember { mutableStateOf<Employee?>(null) }
                    var stack by remember { mutableStateOf<Stack?>(null) }

                    // Launch coroutine to fetch data
                    LaunchedEffect(employeeId, stackId) {
                        employee = workViewModel.getEmployeeById(employeeId)
                        stack = workViewModel.getStackById(stackId)
                    }

                    // Only show the screen once data is loaded
                    if (employee != null && stack != null) {
                        CreateWorkDoneScreen(
                            employee = employee!!,
                            stack = stack!!,
                            onWorkSaved = { workDone ->
                                workViewModel.saveWork(workDone)
                                navController.popBackStack(Screen.WorkDone.route, false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, navController: NavController) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(Screen.Workers, Screen.WorkDone, Screen.Settings)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
        {
            screens.forEachIndexed { index, screen ->
                if (index == 1) {
                    Spacer(Modifier.weight(1f)) // Space for center FAB
                } else {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title,
                                tint = if (currentRoute == screen.route)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }

        // Floating Action Button (Center)
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.SelectEmployee.route)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-36).dp) // Make it float above the bar
                .size(64.dp),
            shape = CircleShape
        ) {
            Icon(Screen.WorkDone.icon, contentDescription = "Work Done", modifier = Modifier.size(28.dp))
        }
    }
}
