package com.example.airdry.ui.screens.workers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.airdry.ui.viewmodels.EmployeeViewModel

@Composable
fun CreateWorkerScreen(navController: NavController, viewModel: EmployeeViewModel) {
    val isServerReachable = viewModel.isServerReachable.collectAsState()

    WorkerForm(
        title = "Register Worker",
        onSubmit = { worker ->
            viewModel.createEmployee(worker)
            if(isServerReachable.value ==true)
            {viewModel.forcedUpload()}
            navController.popBackStack()
        },
        viewModel = viewModel
    )
}
