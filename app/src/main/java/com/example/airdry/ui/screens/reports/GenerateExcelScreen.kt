package com.example.airdry.ui.screens.reports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun GenerateExcelScreen(
    onGenerate: (startDate: LocalDate, endDate: LocalDate) -> Unit = { _, _ -> }
) {
    var startDate by remember { mutableStateOf(LocalDate.now().minusDays(7)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Generate Excel Report", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Start Date: ${startDate.toString()}")
        Spacer(modifier = Modifier.height(4.dp))
        DatePickerButton(
            label = "Pick Start Date",
            selectedDate = startDate,
            onDateSelected = { startDate = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("End Date: ${endDate.toString()}")
        Spacer(modifier = Modifier.height(4.dp))
        DatePickerButton(
            label = "Pick End Date",
            selectedDate = endDate,
            onDateSelected = { endDate = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onGenerate(startDate, endDate)
            }
        ) {
            Text("Generate Excel")
        }
    }
}
