package com.example.airdry.ui.screens.workers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.airdry.database.Entities.Employee
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import java.util.UUID

@Composable
fun EditWorkerScreen(
    navController: NavController,
    employeeId: String,
    viewModel: EmployeeViewModel
) {
    val employeeState = remember { mutableStateOf<Employee?>(null) }

    LaunchedEffect(employeeId) {
        val result = viewModel.getEmployeeById(UUID.fromString(employeeId))
        employeeState.value = result
    }

    val employee = employeeState.value

    if (employee != null) {
        WorkerForm(
            title = "Edit Worker",
            worker = employee,
            onSubmit = { updatedEmployee ->
                viewModel.editEmployee(updatedEmployee)
                navController.popBackStack()
            }
        )
    } else {
        // Loading indicator while data is being fetched
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
