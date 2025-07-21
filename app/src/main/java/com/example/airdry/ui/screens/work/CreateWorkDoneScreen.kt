package com.example.airdry.ui.screens.work

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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


@Composable
fun CreateWorkDoneScreen(
    employee: Employee,
    stack: Stack,
    onWorkSaved: (WorkDone) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        Text("Record Work for ${employee.name}", style = MaterialTheme.typography.titleMedium)
        Text("Stack: ${stack.length}, ${stack.width}")

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount Done") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(onClick = {
            val workDone = WorkDone(
                employeeId = employee.id,
                stackId = stack.id,
                amount = amount.toDoubleOrNull() ?: 0.0,
                ownershipFraction = 1.0f
            )
            onWorkSaved(workDone)
        }) {
            Text("Save Work")
        }
    }
}
