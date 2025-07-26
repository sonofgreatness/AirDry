package com.example.airdry.ui.screens.work

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Employee
import com.example.airdry.database.Entities.Stack
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.util.Enums
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.ui.Alignment

@Composable
fun CreateStackAndWorkScreen(
    allEmployees: List<Employee>,
    defaultEmployee: Employee,
    onSubmit: (Stack, List<WorkDone>) -> Unit
) {
    var selectedType by remember { mutableStateOf(Enums.StackType.STACKTYPE_T1_5) }
    var amount by remember { mutableStateOf("") }
    var isShared by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedEmployees by remember { mutableStateOf(listOf<Employee>()) }
    var selectedItems by remember { mutableStateOf(listOf<String>()) }
    Column(Modifier.padding(16.dp)) {
        Text("New Stack + Work Record", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))
        Text("Select Stack Type", style = MaterialTheme.typography.labelMedium)

        DropdownMenuBox(
            items = Enums.StackType.values().toList(),
            selectedItem = selectedType,
            onItemSelected = { selectedType = it },
            label = { it.displayName }
        )

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Number of Units Done") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isShared,
                onCheckedChange = {
                    isShared = it
                    showDialog = it
                    if (!isShared) selectedEmployees = listOf()
                }
            )
            Text("Is this work shared?")
        }

        if (isShared) {
            Text("Select Employees Sharing the Work", style = MaterialTheme.typography.labelMedium)

            OutlinedTextField(
                value = selectedItems.joinToString(),
                onValueChange = {},
                label = { Text("Select Materials") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()

            )







        } else {
            Text("Assigned to: ${defaultEmployee.name}", style = MaterialTheme.typography.bodyMedium)
        }
        if(showDialog) {
            MultiSelectDialog(
                allItems = listOf("Bricks", "Cement", "Sand", "Blocks"),
                selectedItems = selectedItems,
                onDismiss = { showDialog = false },
                onConfirm = { chosen ->
                    selectedItems = chosen
                    showDialog = false
                }
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val stack = Stack(type = selectedType)
                val totalEmployees = if (isShared) selectedEmployees.size else 1
                val fraction = 1.0f / totalEmployees
                val employees = if (isShared) selectedEmployees else listOf(defaultEmployee)

                val workList = employees.map { emp ->
                    WorkDone(
                        employeeId = emp.id,
                        stackId = stack.id,
                        amount = (amount.toDoubleOrNull() ?: 0.0),
                        ownershipFraction = fraction
                    )
                }

                onSubmit(stack, workList)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = amount.toDoubleOrNull() != null && (!isShared || selectedEmployees.isNotEmpty())
        ) {
            Text("Save Stack and Work")
        }
    }
}
