package com.example.airdry.ui.screens.workers

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.airdry.ui.viewmodels.EmployeeViewModel

@Composable
fun CreateWorkerScreen(navController: NavController, viewModel: EmployeeViewModel) {
    WorkerForm(
        title = "Register Worker",
        onSubmit = { worker ->
            viewModel.createEmployee(worker)
            viewModel.forcedUpload()
            navController.popBackStack()
        },
        viewModel = viewModel
    )
}
