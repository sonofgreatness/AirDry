package com.example.airdry.ui.screens.work

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.airdry.ui.viewmodels.RecordWorkViewModel
import java.util.UUID

@Composable
fun EditWorkScreen(viewModel: RecordWorkViewModel, workDoneId: UUID?, navController: NavController) {
    // Trigger loading of existing work data
    LaunchedEffect(workDoneId) {
        // TODO: Call viewModel.loadWorkForEdit(workDoneId)
    }

    WorkForm(
        viewModel = viewModel,
        onSubmit = { state ->
            // TODO: Call viewModel.updateExistingWork(state)
            println("Updating Work: ${state.id}")
        },
        onDelete = {
            // TODO: Call viewModel.deleteWork(workDoneId)
            println("Deleting Work: $workDoneId")
        },
        isEditMode = true,
        navController
    )
}