package com.example.airdry.ui.screens.work

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.airdry.ui.viewmodels.WorkViewModel

@Composable
fun EmployeeSelectionScreen(viewModel: WorkViewModel, onSelected: () -> Unit) {

    val employees = viewModel.employees.observeAsState(emptyList()).value

    LazyColumn {
        items(employees) { employee ->
            Card(
                onClick = {
                    viewModel.selectEmployee(employee)
                    onSelected()
                },
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            ) {
                Text(text = "${employee.name} ${employee.surname}", modifier = Modifier.padding(16.dp))
            }
        }
    }
}