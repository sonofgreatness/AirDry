package com.example.airdry.ui.screens.work

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.airdry.ui.viewmodels.RecordWorkViewModel

@Composable
fun RecordWorkScreen(viewModel: RecordWorkViewModel, navController: NavController) {

    val isServerReachable = viewModel.isServerReachable.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearFormState()
    }

    WorkForm(
        viewModel = viewModel,
        onSubmit = { state ->
            viewModel.recordWork(state)
            Log.d("Transaction create", "server :${isServerReachable.value}")
           if (isServerReachable.value ==true){
               viewModel.forcedUpload()
           }
       },
        isEditMode = false,
        navController = navController
    )
}

