package com.example.airdry.ui.screens.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> MultiSelectDropdown(
    allItems: List<T>,
    selectedItems: List<T>,
    onSelectionChange: (List<T>) -> Unit,
    label: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selectedItems.joinToString(", ") { label(it) },
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Employees") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            allItems.forEach { item ->
                // Determine if item is selected
                val isChecked = selectedItems.contains(item)

                DropdownMenuItem(
                    onClick = {
                        val updated = if (isChecked) {
                            selectedItems - item
                        } else {
                            selectedItems + item
                        }
                        onSelectionChange(updated)
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = null // no-op because we use onClick above
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label(item))
                        }
                    }
                )
            }
        }
    }
}
