package com.example.airdry.ui.screens.work

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Employee
import com.example.airdry.ui.viewmodels.WorkViewModel

@Composable
fun EmployeeSelectionScreen(viewModel: WorkViewModel, onSelected: () -> Unit) {

    val employees = viewModel.employees.observeAsState(emptyList()).value


    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select Employee (Create Stack)", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))
    LazyColumn {
        items(employees) { employee ->
            SelectEmployeeCreateStackCard(viewModel, employee, onSelected)
        }
    }

    }
}

@Composable
private fun SelectEmployeeCreateStackCard(
    viewModel: WorkViewModel,
    employee: Employee,
    onSelected: () -> Unit
) {
    Card(
        onClick = {
            viewModel.selectEmployee(employee)
            onSelected()
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = "${employee.name} ${employee.surname}", modifier = Modifier.padding(16.dp))
    }
}


