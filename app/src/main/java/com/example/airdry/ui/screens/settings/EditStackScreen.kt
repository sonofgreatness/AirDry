package com.example.airdry.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.airdry.ui.viewmodels.StackViewModel
import com.example.airdry.util.UtilityFunctions.Companion.toUUID

@Composable
fun EditStackScreen(

    navController: NavController,
    stackId: String,
    viewModel: StackViewModel)
{
    val stackState by viewModel.loadedStack.collectAsState()
    LaunchedEffect(stackId) {
        viewModel.loadStackById(stackId.toUUID())
    }
    val stack = stackState?.stack
    val stackCode  = stackState?.stackCode
    if (stack!= null) {
        StackForm(
            title = "Stack Details",
            stack = stack,
            stackCode =stackCode,
            onSubmit = {
               navController.popBackStack()
            }
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