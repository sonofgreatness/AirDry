package com.example.airdry.ui.screens.workers

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.airdry.ui.viewmodels.EmployeeViewModel

@Composable
fun CreateWorkerScreen(navController: NavController, viewModel: EmployeeViewModel) {
    WorkerForm(
        title = "Create Worker",
        onSubmit = { worker ->
            viewModel.createEmployee(worker)
            navController.popBackStack()
        }
    )
}
