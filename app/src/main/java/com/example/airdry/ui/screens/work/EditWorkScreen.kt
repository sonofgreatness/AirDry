package com.example.airdry.ui.screens.work

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.viewmodels.RecordWorkViewModel
import java.util.UUID

@Composable
fun EditWorkScreen(viewModel: RecordWorkViewModel, workDoneId: UUID?, navController: NavController) {
    LaunchedEffect(workDoneId) {
        viewModel.loadWorkForEdit(workDoneId)
    }

    WorkForm(
        viewModel = viewModel,
        onSubmit = { state ->
          viewModel.updateExistingWork(state)
        },
        onDelete = {
            workDoneId?.let{
                viewModel.deleteWork(it)
                navController.navigate(Screen.SelectEmployee.route)
            }
        },
        isEditMode = true,
        navController
    )
}