package com.example.airdry.ui.screens.work

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.ui.viewmodels.EmployeeViewModel

@Composable
fun MultiSelectDialog(
    viewModel: EmployeeViewModel,
    selectedItems: List<NestedAppUser>,
    onDismiss: () -> Unit,
    onConfirm: (List<NestedAppUser>) -> Unit,
    defaultAppUser: NestedAppUser
) {
    val selected = remember {
        mutableStateListOf<NestedAppUser>().apply {
            addAll(selectedItems)
            if (defaultAppUser !in this) add(defaultAppUser)
        }
    }
    val searchTerm = remember { mutableStateOf("") }
    val allItems by viewModel.filteredEmployees.observeAsState(emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Select Workers Sharing") },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = searchTerm.value,

                        onValueChange = { searchTerm.value = it
                            viewModel.updateFilteredEmployees(it)
                        },
                        placeholder = { Text("Search...") },
                        singleLine = true,
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape))

                    Icon(Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                searchTerm.value = ""
                                viewModel.updateFilteredEmployees("")
                            })}
                allItems.forEach { item ->
                    val isChecked = item in selected
                    val isDefault = item == defaultAppUser

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (!isDefault) Modifier.clickable {
                                    if (isChecked) selected.remove(item) else selected.add(item)
                                } else Modifier // Do not allow clicks on defaultEmployee
                            )
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = null,
                            enabled = !isDefault
                        )
                        Text(
                            text = item.toString() + if (isDefault) " (default)" else ""
                        )
                    }
                }
            }
        }
    )
}
