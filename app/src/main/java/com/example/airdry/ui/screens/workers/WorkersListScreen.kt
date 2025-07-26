package com.example.airdry.ui.screens.workers



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.database.Entities.Employee
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.nav.Screen
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun WorkersListScreen(
    navController: NavController,
    viewModel: EmployeeViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.getAllEmployees()
    }
    val workers by viewModel.allEmployees.observeAsState(emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Employees", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(workers) { worker ->
                WorkerCard(worker = worker, onClick = {
                    navController.navigate(Screen.EditWorker.withArgs(worker.id.toString()))
                })
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate(Screen.CreateWorker.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Worker")
        }
    }
}

@Composable
fun WorkerCard(worker: Employee, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "${worker.name} ${worker.surname.orEmpty()}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Phone: ${worker.phoneNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "National ID: ${worker.nationalId}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (worker.isActive) "Active" else "Inactive",
                style = MaterialTheme.typography.labelSmall,
                color = if (worker.isActive) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

