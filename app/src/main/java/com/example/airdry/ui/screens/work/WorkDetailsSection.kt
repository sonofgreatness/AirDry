package com.example.airdry.ui.screens.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.airdry.ui.viewmodels.WorkFormState
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun WorkDetailsSection(
    formState: WorkFormState,
    onUpdateDate: (Long) -> Unit,
    onShowDatePicker: (Boolean) -> Unit
) {
    val date = java.time.Instant.ofEpochMilli(formState.workDate)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()

    val formattedDate = date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Work Metadata", style = MaterialTheme.typography.titleLarge)
        Divider(Modifier.padding(vertical = 8.dp))

        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            label = { Text("Work Date") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDatePicker(true) },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Date") }
        )
    }
}