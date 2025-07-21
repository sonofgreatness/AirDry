package com.example.airdry.ui.screens.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
    fun <T> DropdownMenuBox(
        items: List<T>,
        selectedItem: T?,
        onItemSelected: (T) -> Unit,
        label: (T) -> String
    ) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            OutlinedTextField(
                value = selectedItem?.let { label(it) } ?: "",
                onValueChange = {},
                label = { Text("Select") },
                modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                enabled = false,
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
            )

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        text = {
                            Text(label(item))
                        },
                    )
                }
            }
        }
    }

