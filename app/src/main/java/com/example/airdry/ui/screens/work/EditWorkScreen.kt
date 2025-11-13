package com.example.airdry.ui.screens.work

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.viewmodels.RecordWorkViewModel
import java.util.UUID

@Composable
fun EditWorkScreen(viewModel: RecordWorkViewModel, workDoneId: UUID?, navController: NavController) {



    val isServerReachable = viewModel.isServerReachable.collectAsState()
    LaunchedEffect(workDoneId) {
        viewModel.loadWorkForEdit(workDoneId)
    }


    WorkForm(
        viewModel = viewModel,
        onSubmit = { state ->
          viewModel.updateExistingWork(state)
            Log.d("Transaction update", "server :${isServerReachable.value}")
            if(isServerReachable.value ==true)
            {
                viewModel.forcedUpdate()

            }
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