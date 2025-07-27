package com.example.airdry.ui.screens.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Employee
@Composable
fun MultiSelectDialog(
    allItems: List<Employee>,
    selectedItems: List<Employee>,
    onDismiss: () -> Unit,
    onConfirm: (List<Employee>) -> Unit,
    defaultEmployee: Employee
) {
    val selected = remember {
        mutableStateListOf<Employee>().apply {
            // Ensure defaultEmployee is always included
            addAll(selectedItems)
            if (defaultEmployee !in this) add(defaultEmployee)
        }
    }

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
                allItems.forEach { item ->
                    val isChecked = item in selected
                    val isDefault = item == defaultEmployee

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
