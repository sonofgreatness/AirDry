package com.example.airdry.ui.screens.workers




import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.theme.Typography
import com.example.airdry.ui.viewmodels.EmployeeViewModel

@Composable
fun WorkersListScreen(
    navController: NavController,
    viewModel: EmployeeViewModel
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.superWork()
        viewModel.getAllEmployees()
    }
    val workers by viewModel.allEmployees.observeAsState(emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Employees", style = Typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.CreateWorker.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Worker")
        }

        Spacer(modifier = Modifier.height(12.dp))
         if(workers.isEmpty()){
             Box(
                 Modifier.fillMaxSize(),
                 contentAlignment = Alignment.Center
             ) {
                 Text("No workers found", style = MaterialTheme.typography.bodyLarge)
             }
         }else{
        LazyColumn {
            itemsIndexed (workers){  index, worker ->
                WorkerCard(
                    worker = worker, onClick = {
                        navController.navigate(Screen.EditWorker.withArgs(worker.appUser.id.toString()))
                    },
                    employeeViewModel = viewModel
                )
            }
        }
    }
    }
}
@Composable
fun WorkerCard(
    worker: NestedAppUser,
    onClick: () -> Unit,
    employeeViewModel: EmployeeViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            // Row for name and delete icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${worker.appUser.name} ${worker.appUser.surname.orEmpty()}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (worker.appUser.isPendingInsert ==true){
                IconButton(
                    onClick = {
                        employeeViewModel.deleteEmployee(worker.appUser)
                        //show loader
                        //reload  list
                        employeeViewModel.getAllEmployees()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Worker",
                        tint = MaterialTheme.colorScheme.onSurface,

                    )
                }

                IconButton(
                    onClick = {
                        employeeViewModel.forcedUpload()
                        //show loader
                        //reload  list
                        employeeViewModel.getAllEmployees()
                    },
                    enabled = worker.appUser.isPendingInsert==true
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Delete Worker",
                        tint = MaterialTheme.colorScheme.onSurface)
                }
             }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Phone: ${worker.appUser.phoneNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Department: ${worker.department?.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "Section: ${worker.section?.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (worker.appUser.isActive == true) "Active" else "Inactive",
                style = MaterialTheme.typography.labelSmall,
                color = if (worker.appUser.isActive == true) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
