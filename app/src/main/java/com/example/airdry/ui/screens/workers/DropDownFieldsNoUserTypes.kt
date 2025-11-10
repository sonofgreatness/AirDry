package com.example.airdry.ui.screens.workers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.Section
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownFieldsNoUserTypes  (selectedDepartment: Department?, selectedSection: Section?,
expandedUserType: Boolean, onExpandUserType: (Boolean) -> Unit,
expandedDepartment: Boolean, onExpandDepartment: (Boolean) -> Unit,
expandedSection: Boolean, onExpandSection: (Boolean) -> Unit,departments: List<Department>, sections: List<Section>,
onDepartmentChange:(id :UUID) ->Unit,
onSectionChange :(id :UUID) -> Unit,
isEditMode: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // === Department Dropdown ===
        ExposedDropdownMenuBox(
            expanded = expandedDepartment,
            onExpandedChange = { onExpandDepartment(!expandedDepartment) }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedDepartment?.displayName ?: "Select department",
                onValueChange = {},
                label = { Text("Department", style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                enabled = !isEditMode
            )
            ExposedDropdownMenu(
                expanded = expandedDepartment,
                onDismissRequest = { onExpandDepartment(false) }
            ) {
                departments.forEach { dept ->
                    DropdownMenuItem(
                        text = { Text(dept.displayName, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            onExpandDepartment(false)
                            onDepartmentChange(dept.id)
                        }
                    )
                }
            }
        }

        // === Section Dropdown ===
        ExposedDropdownMenuBox(
            expanded = expandedSection,
            onExpandedChange = { onExpandSection(!expandedSection) }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedSection?.displayName ?: "Select section",
                onValueChange = {},
                label = { Text("Section", style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                enabled = !isEditMode
            )
            ExposedDropdownMenu(
                expanded = expandedSection,
                onDismissRequest = { onExpandSection(false) }
            ) {
                sections.forEach { section ->
                    DropdownMenuItem(
                        text = { Text(section.displayName, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            onExpandSection(false)
                            onSectionChange(section.id)
                        }
                    )
                }
            }
        }
    }
}
