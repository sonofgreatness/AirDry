package com.example.airdry.ui.screens.work

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.viewmodels.WorkViewModel
import java.time.LocalDate

@Composable
fun EmployeeSelectionScreen(
    viewModel: WorkViewModel,
    employeeViewModel: EmployeeViewModel,
    onSelected: () -> Unit,

) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        employeeViewModel.getAllEmployees()
        employeeViewModel.updateFilteredEmployees("")
        viewModel.loadWorkDoneWithUsersAndStacks()
    }
    val searchTerm = remember { mutableStateOf("") }
    val employees by employeeViewModel.filteredEmployees.observeAsState(emptyList())
    val nestedWork = viewModel.workDoneWithUsersAndStack.observeAsState(emptyList()).value
    val today = remember { LocalDate.now() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = searchTerm.value,

                onValueChange = { searchTerm.value = it
                    employeeViewModel.updateFilteredEmployees(it)
                },
                placeholder = { Text("Search...") },
                singleLine = true,
                shape = CircleShape,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape),

            )

            Icon(Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        searchTerm.value = ""
                        employeeViewModel.updateFilteredEmployees("")
                    })
        }



        if (employees.isEmpty()){
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No workers found", style = MaterialTheme.typography.bodyLarge)
            }
        }
        else{
        LazyColumn {
            itemsIndexed(employees) { index, employee ->
                val workGroups = emptyMap<LocalDate, List<WorkDoneWithUsersAndStacks>>()

                SelectEmployeeCreateStackCard(
                    viewModel = viewModel,
                    appUser = employee,
                    workGroups = workGroups,
                    today = today,
                    onSelected = onSelected
                )
            }
        }
    }
    }
}
@Composable
private fun SelectEmployeeCreateStackCard(
    viewModel: WorkViewModel,
    appUser: AppUser,
    workGroups: Map<LocalDate, List<WorkDoneWithUsersAndStacks>>,
    today: LocalDate,
    onSelected: () -> Unit
) {
    Card(
        onClick = {
            viewModel.selectEmployee(appUser)
            onSelected()
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = "${appUser.name} ${appUser.surname ?: ""}", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (workGroups.isEmpty()) {
                Text("No work recorded.", style = MaterialTheme.typography.bodySmall)
            } else {
                workGroups.toSortedMap(compareByDescending { it }).forEach { (date, workList) ->
                    Column(Modifier.padding(vertical = 4.dp)) {
                        val isToday = date == today
                        Text(
                            text = if (isToday) "Today" else date.toString(),
                            style = MaterialTheme.typography.labelLarge
                        )
                        if (workList.isNotEmpty()) {
                            workList.forEach { workWithStack ->
                                val stackType = "workWithStack.stacks"
                                val amount = 0
                                val fraction = 0.6
                                Text(" - $stackType | Amount: $amount | %Owned: ${String.format("%.2f", (fraction * 100.0))}%", style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            Text(" - No work recorded", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
