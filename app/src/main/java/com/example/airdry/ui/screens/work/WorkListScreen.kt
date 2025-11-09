package com.example.airdry.ui.screens.work

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.viewmodels.WorkViewModel
import java.time.LocalDate

@Composable
fun WorkListScreen(
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
        viewModel.loadWorkDoneWithUsersAndStacks()
    }

    val nestedWork = viewModel.workDoneWithUsersAndStack.observeAsState(emptyList()).value

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            itemsIndexed(nestedWork) { index, work ->
                val workGroups = emptyMap<LocalDate, List<WorkDoneWithUsersAndStacks>>()
                Text("${work}")
            }
        }

    }
}
