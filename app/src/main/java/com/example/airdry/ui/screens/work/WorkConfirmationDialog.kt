package com.example.airdry.ui.screens.work

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.ui.viewmodels.SelectedStackItem
import com.example.airdry.ui.viewmodels.WorkFormState

// --- NEW CONFIRMATION DIALOG COMPOSABLE ---

@Composable
fun WorkConfirmationDialog(

    onDismiss: () -> Unit,
    state: WorkFormState,
    onConfirmSave: (state: WorkFormState) -> Unit
) {
   val  selectedUsers: List<NestedAppUser> = state.selectedUsers
   val  selectedStacks: List<SelectedStackItem> =state.selectedStacks

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Confirm Work Entry", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    "Please review the work details before saving.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))

                // --- Workers Summary ---
                Text("Workers (${selectedUsers.size}):", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Column {
                        if (selectedUsers.isEmpty()) {
                            Text("No workers selected.")
                        } else {
                            selectedUsers.forEach { nestedUser ->
                                val user = nestedUser.appUser

                                Text("• ${user.name} ${user.surname} ${user.phoneNumber}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // --- Stacks Summary ---
                val totalPicks = selectedStacks.sumOf { it.stack.stack.volume ?: 0.0}
                Text("Stacks Entered (Total: ${String.format("%.2f m³", totalPicks)} cubes):", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Column {
                        if (selectedStacks.isEmpty()) {
                            Text("No stacks selected.")
                        } else {
                            selectedStacks.forEach { detail ->
                                val stack = detail.stack.stack
                                val stackCode  = detail.stack.stackCode
                                Text("•${stack.displayName}  ${stackCode.displayName}  -${stackCode.displayCode}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirmSave(state)}) {
                Text("Confirm and Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}