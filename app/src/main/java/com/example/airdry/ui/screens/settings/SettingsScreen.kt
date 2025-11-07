package com.example.airdry.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.theme.BrownDriftwood
import com.example.airdry.ui.theme.ForestGreen
import com.example.airdry.ui.theme.GreenMain
import com.example.airdry.ui.theme.GreenNile
import com.example.airdry.ui.theme.Typography
import com.example.airdry.ui.viewmodels.SettingsViewModel
import com.example.airdry.util.operational_states.SyncStatus

@Composable
fun Settings(
        navController: NavController,
        viewModel : SettingsViewModel,
        darkThemeEnabled: Boolean,
        onToggleTheme: (Boolean) -> Unit
    ){
    val context = LocalContext.current
    val syncStatus by viewModel.syncStatus.collectAsState()
    val isSyncing = syncStatus is SyncStatus.Working

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
            ) {
                Text(
                    "Main Settings",
                    modifier = Modifier.padding(16.dp).align(
                        Alignment.CenterHorizontally
                    ),
                    style = Typography.titleLarge

                )
                GreenDivider()
                Text(
                    "Check For Updates", modifier = Modifier.padding(16.dp).align(
                        Alignment.Start
                    ).clickable {
                        navController.navigate(route = Screen.ManageUpdates.route)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface)
                GreenDivider()
                ProgressSettingItem(
                    icon = Icons.Default.CloudDownload,
                    title = "Update Application Data",
                    currentStatus = when(syncStatus) {
                        SyncStatus.Idle -> "Ready to synchronize data"
                        SyncStatus.Complete -> "Synchronization complete"
                        is SyncStatus.Working -> (syncStatus as SyncStatus.Working).message
                        is SyncStatus.Error -> (syncStatus as SyncStatus.Error).message
                    },
                    isWorking = isSyncing,
                    onClick = viewModel::synchronizeData
                )
                GreenDivider()
                SettingItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    description = "Use a darker color scheme for night-time work",
                    trailing = {
                        // Stateless switch (drives parent via callback)
                        Switch(
                            checked = darkThemeEnabled,
                            onCheckedChange = onToggleTheme,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GreenNile,
                                checkedTrackColor = GreenMain,
                                uncheckedThumbColor = BrownDriftwood
                            )
                        )
                    }
                )
                GreenDivider()
                GreenDivider()
                Text(
                    "View Stacks Library",
                    modifier = Modifier.padding(16.dp).align(Alignment.Start).clickable {
                        navController.navigate(route = Screen.ManageStacks.route)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface

                )
                GreenDivider()
            }
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                GreenDivider()
                Text(
                    "About",
                    modifier = Modifier.padding(16.dp).align(Alignment.Start).clickable {
                        navController.navigate(route = Screen.About.route)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface

                )
                GreenDivider()
            }
        }

    }

@Composable
fun ProgressSettingItem(
    icon: ImageVector,
    title: String,
    currentStatus: String,
    isWorking: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Clickable only when not syncing
            .clickable(enabled = !isWorking, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = rememberVectorPainter(icon),
            contentDescription = null,
            modifier = Modifier.size(34.dp),
            tint = if (isWorking) GreenMain else MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                color = if (isWorking) GreenMain else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = currentStatus,
                style = MaterialTheme.typography.labelMedium,
                color = if (isWorking) GreenNile else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(Modifier.width(8.dp))
        if (isWorking) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp,
                color = GreenMain
            )
        }
    }
}

@Composable
fun GreenDivider(){
    Divider(
        color = ForestGreen,
        thickness = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )

}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold,
                fontSize = 15.sp))
            Text(description, style = MaterialTheme.typography.labelMedium)
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        }
    }
}