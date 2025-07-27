package com.example.airdry.ui.screens.work

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Employee
import com.example.airdry.database.Entities.Stack
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.util.Enums

@Composable
fun CreateStackAndWorkScreen(
    allEmployees: List<Employee>,
    defaultEmployee: Employee,
    onSubmit: (Stack, List<WorkDone>) -> Unit
) {
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf(Enums.StackType.STACKTYPE_T1_5) }
    var amount by remember { mutableStateOf("") }
    var isShared by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedEmployees by remember {
        mutableStateOf(listOf<Employee>(defaultEmployee))
    }

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
                    selectedEmployees = listOf(defaultEmployee) // always keep default
                }
            )
            Text("Is this work shared?")
        }

        if (isShared) {
            Text("Select Employees Sharing the Work", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = selectedEmployees.joinToString { it.name },
                onValueChange = {},
                label = { Text("Selecting Workers Sharing...") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text("Assigned to: ${defaultEmployee.name}", style = MaterialTheme.typography.bodyMedium)
        }

        if (showDialog) {
            MultiSelectDialog(
                allItems = allEmployees,
                selectedItems = selectedEmployees,
                defaultEmployee = defaultEmployee,
                onDismiss = { showDialog = false },
                onConfirm = { chosen ->
                    selectedEmployees = chosen
                    showDialog = false
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        val isAmountValid = amount.toDoubleOrNull() != null
        val isSelectionValid = !isShared || selectedEmployees.size > 1

        Button(
            onClick = {
                if (!isSelectionValid) {

                    Toast.makeText(
                        context,
                        "Please select at least one other employee besides the default when sharing work.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {

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

                } },
            modifier = Modifier.fillMaxWidth(),
            enabled = isAmountValid
        ) {
            Text("Save Stack and Work")
        }
    }
}

