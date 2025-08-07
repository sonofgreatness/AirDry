package com.example.airdry.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.airdry.ui.viewmodels.WorkViewModel

@Composable
fun CreateStackScreen(
    navController: NavController,
    viewModel: WorkViewModel
){
    StackForm("Create Stack",
        onSubmit = { stack ->
            viewModel.createStack(stack)
            navController.popBackStack()
        }
    )
}