package com.example.airdry.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.example.airdry.ui.viewmodels.AuthViewModel
import com.example.airdry.util.operational_states.UiState

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val focusManager: FocusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        authViewModel.resetState()
        authViewModel.clearFields()
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (uiState !is UiState.Success) {
            // --- Login Form ---
            OutlinedTextField(
                value = authViewModel.username,
                onValueChange = { authViewModel.onUsernameChange(it) },
                label = { Text("Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
            )
            PasswordField(
                password = authViewModel.password,
                onPasswordChange = { authViewModel.onPasswordChange(it) },
                focusManager = focusManager,
                onDone = { authViewModel.login() }
            )
            Button(
                onClick = { authViewModel.login() },
                enabled = uiState !is UiState.Loading
            ) {
                Text(if (uiState is UiState.Loading) "Logging in..." else "Login")
            }
        }
        when (uiState) {
            is UiState.Error -> Text(
                (uiState as UiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            is UiState.Success -> {
                Text(
                    "✅ Logged in successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        authViewModel.resetState()
                        onLoginSuccess()
                    }
                ) {
                    Text("Continue")
                }
            }

            else -> {}
        }
    } }

}
