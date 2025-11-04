package com.example.airdry.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.SignalCellularOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirDryTopAppBar(
    title: String,
    showBackIcon: Boolean,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    isServerUp: Boolean,
    isUserLoggedIn: Boolean
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // --- 1. Connectivity and Login Status with Dropdown ---

            // Local state to control the visibility of the dropdown menu
            var showStatusDropdown by remember { mutableStateOf(false) }

            // Determine status icon, color, and explanatory text
            val (statusIcon, statusColor, statusMessage) = remember(isServerUp, isUserLoggedIn) {
                when {
                    !isServerUp -> Triple(
                        Icons.Default.SignalCellularOff,
                        Color.Red,
                        "Server is unreachable. Please check your network or server status."
                    )
                    isServerUp && !isUserLoggedIn -> Triple(
                        Icons.Default.SignalCellularConnectedNoInternet0Bar,
                        Color.Yellow.copy(alpha = 0.8f), // Use a yellow/amber for warning
                        "Connected to server, but user is logged out."
                    )
                    isUserLoggedIn -> Triple(
                        Icons.Default.CloudDone,
                        Color.Green,
                        "Connected and Logged in."
                    )
                    else -> Triple(
                        Icons.Default.SignalCellularOff,
                        Color.Red,
                        "Unknown status."
                    )
                }
            }
            Box(modifier = Modifier.padding(end = 4.dp)) {
                IconButton(onClick = { showStatusDropdown = true }) {
                    Crossfade(targetState = statusIcon, label = "StatusIconTransition") { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = "Connection Status: $statusMessage",
                            tint = statusColor,
                            modifier = Modifier.padding(end = 4.dp))
                    }
                }
                DropdownMenu(
                    expanded = showStatusDropdown,
                    onDismissRequest = { showStatusDropdown = false }
                ) {DropdownMenuItem(
                        text = { Text(statusMessage) },
                        onClick = { showStatusDropdown = false }, // Close on click
                        leadingIcon = {
                            Icon(imageVector = statusIcon, contentDescription = null, tint = statusColor)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}