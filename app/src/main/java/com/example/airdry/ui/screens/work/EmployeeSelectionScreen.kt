package com.example.airdry.ui.screens.work

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.airdry.R
import com.example.airdry.database.Entities.Employee
import com.example.airdry.database.Entities.WorkWithStack
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.viewmodels.WorkViewModel
import com.example.airdry.util.toLocalDateTime
import java.time.LocalDate

@Composable
fun EmployeeSelectionScreen(
    viewModel: WorkViewModel,
    employeeViewModel: EmployeeViewModel,
    onMovetoGenerateExcel: () -> Unit,
    onSelected: () -> Unit,

) {

    LaunchedEffect(Unit) {
        employeeViewModel.getAllEmployees()
        viewModel.loadWorkDoneWithStack()
    }

    val employees = employeeViewModel.allEmployees.observeAsState(emptyList()).value
    val workMap = viewModel.workDoneWithStack.observeAsState(emptyMap()).value
    val today = remember { LocalDate.now() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Select Worker (Create Stack)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // ✅ Clickable Excel Icon
            Image(
                painter = painterResource(id =
                    R.drawable.excel_logo),
                contentDescription = "Generate Excel",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onMovetoGenerateExcel()}
                    .padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(employees) { employee ->
                val workGroups = workMap[employee.id]?.groupBy {
                    it.workDone.dateWorked.toLocalDateTime().toLocalDate()
                } ?: emptyMap()

                SelectEmployeeCreateStackCard(
                    viewModel = viewModel,
                    employee = employee,
                    workGroups = workGroups,
                    today = today,
                    onSelected = onSelected
                )
            }
        }
    }
}


@Composable
private fun SelectEmployeeCreateStackCard(
    viewModel: WorkViewModel,
    employee: Employee,
    workGroups: Map<LocalDate, List<WorkWithStack>>,
    today: LocalDate,
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
        Column(Modifier.padding(16.dp)) {
            Text(text = "${employee.name} ${employee.surname ?: ""}", style = MaterialTheme.typography.titleMedium)
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
                                val stackType = workWithStack.stack.type
                                val amount = workWithStack.workDone.amount
                                val fraction = workWithStack.workDone.ownershipFraction
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
