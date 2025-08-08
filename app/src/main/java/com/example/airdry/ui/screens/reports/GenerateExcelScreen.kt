package com.example.airdry.ui.screens.reports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.airdry.ui.viewmodels.WorkViewModel
import com.example.airdry.util.operational_states.ExcelReportUiState
import java.time.LocalDate

@Composable
fun GenerateExcelScreen(
    viewModel: WorkViewModel,
    onMoveToNextScreen: () -> Unit = {}
) {
    val uiState by viewModel.uiState.observeAsState(ExcelReportUiState())
    var startDate by remember { mutableStateOf(LocalDate.now().minusDays(7)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Generate Excel Report",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(20.dp))

        DatePickerButton(
            label = "Pick Start Date (${startDate})",
            selectedDate = startDate,
            onDateSelected = { startDate = it }
        )

        Spacer(Modifier.height(12.dp))

        DatePickerButton(
            label = "Pick End Date (${endDate})",
            selectedDate = endDate,
            onDateSelected = { endDate = it }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.generatExcelReport(startDate, endDate) },
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("Generating…")
            } else {
                Text("Generate Excel")
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onMoveToNextScreen,
            enabled = !uiState.isLoading
        ) {
            Text("View Past Reports")
        }

        Spacer(Modifier.height(24.dp))

        // Show status messages
        uiState.successMessage?.let {
            Text(it, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
        }

        uiState.errorMessage?.let {
            Text(it, color = Color(0xFFC62828), fontWeight = FontWeight.Medium)
        }
    }
}
