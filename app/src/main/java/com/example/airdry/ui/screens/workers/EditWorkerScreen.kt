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
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import java.util.UUID

@Composable
fun EditWorkerScreen(
    navController: NavController,
    employeeId: String,
    viewModel: EmployeeViewModel,
) {
    val appUserState = remember { mutableStateOf<NestedAppUser?>(null) }

    LaunchedEffect(employeeId) {
        val result = viewModel.getEmployeeById(UUID.fromString(employeeId))
        appUserState.value = result
    }

    val employee = appUserState.value?.appUser

    if (employee != null) {
        WorkerForm(
            title = "View Worker Details",
            worker = employee,
            onSubmit = { updatedEmployee ->
                navController.popBackStack()
            },
            viewModel,
            isEditMode = true
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
