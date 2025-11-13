package com.example.airdry.ui.screens.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.ui.viewmodels.SelectedStackItem

@Composable
fun UserSelectRow(employee: NestedAppUser, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("${employee.appUser.name} ${employee.appUser.surname}", fontWeight = FontWeight.SemiBold)
        if (isSelected) {

            Icon(Icons.Default.Check, "Selected", tint = MaterialTheme.colorScheme.primary)
        } else {
            Icon(Icons.Default.Add, "Select")
        }
    }
}

@Composable
fun UserPreviewRow(user: NestedAppUser, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("👷 ${user.appUser.name} ${user.appUser.surname}")
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.RemoveCircle, "Remove User", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun StackPreviewRow(item: SelectedStackItem, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("📦 ${item.stack.stack.displayName}", fontWeight = FontWeight.SemiBold)
            Text("Code: ${item.stack.stackCode.displayCode}", style = MaterialTheme.typography.labelSmall)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(String.format("%.3f m³", item.stack.stack.volume), fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, "Remove Stack", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackDropdown(
    filteredStacks: List<NestedStack>,
    selectedStack: NestedStack?,
    searchText: String,
    onSelect: (NestedStack) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = "${selectedStack?.stack?.displayName?: ""} ${selectedStack?.stackCode?.displayName?: ""} -${selectedStack?.stackCode?.displayCode?:"select here"}" ?: searchText,
            onValueChange = {
                //onSearch(it)
                },
            label = { Text("Select Stack") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filteredStacks.forEach { stack ->
                DropdownMenuItem(
                    text = {
                        //Text("${stack.stack.displayName} (${stack.stackCode.displayCode})")
                        Text(
                            text = buildString {
                                val safeDisplayName = stack.stack.displayName.ifBlank { "Unnamed Stack" }
                                val safeStackCode = stack.stackCode.displayCode.ifBlank { "N/A" }
                                val safeStackCodeName = stack.stackCode.displayName.ifBlank { "N/A" }

                                // Header: Stack name
                                append("📦 $safeDisplayName\n")

                                // Subheader: Stack code + name
                                append("🆔 $safeStackCode - $safeStackCodeName\n")

                                // Dimensions and volume
                                append("📏 Dims: ${"%.3f".format(stack.stack.length)}×${"%.3f".format(stack.stack.height)}×${"%.3f".format(stack.stack.thickness)}\n")
                                append("🔢 Pieces: ${stack.stack.numberOfPieces}  |  🔹 Cubes: ${"%.2f".format(stack.stack.volume ?: 0.0)} m³")
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                           },
                    onClick = {
                        onSelect(stack)
                        expanded = false
                    }
                )
            }
        }
    }
}