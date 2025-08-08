package com.example.airdry.ui.screens.settings

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
import com.example.airdry.database.Entities.Stack
import com.example.airdry.ui.viewmodels.WorkViewModel
import java.util.UUID

@Composable
fun EditStackScreen(

    navController: NavController,
    stackId: String,
    viewModel: WorkViewModel


) {
    val stackState = remember { mutableStateOf<Stack?>(null) }

    LaunchedEffect(stackId) {
        val result = viewModel.getStackById(UUID.fromString(stackId))
        stackState.value = result
    }

    val stack = stackState.value

    if (stack!= null) {
        StackForm(
            title = "Edit Stack",
            stack = stack,
            onSubmit = { updatedStack ->
                viewModel.editStack(updatedStack)
                navController.popBackStack()
            }
            ,
            onDelete = {
                stack ->
                viewModel.deleteStack(stack)
                navController.popBackStack()
            },
            showDeleteButton = true
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