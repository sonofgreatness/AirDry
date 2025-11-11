package com.example.airdry.ui.screens.work

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.airdry.ui.viewmodels.RecordWorkViewModel

@Composable
fun RecordWorkScreen(viewModel: RecordWorkViewModel, navController: NavController) {


    LaunchedEffect(Unit) {
        viewModel.clearFormState()
    }

    WorkForm(
        viewModel = viewModel,
        onSubmit = { state ->
            viewModel.recordWork(state)},
        isEditMode = false,
        navController = navController
    )
}

