package com.example.airdry.ui.screens.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.airdry.ui.viewmodels.WorkFormState
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkDetailsSection(
    formState: WorkFormState,
    onUpdateDate: (Long) -> Unit, // Not used in this snippet but kept for completeness
    onShowDatePicker: (Boolean) -> Unit
) {
    // Note: Removed @ExperimentalTime opt-in as it's not needed for java.time classes
    val date = java.time.Instant.ofEpochMilli(formState.workDate)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()

    val formattedDate = date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Work Metadata", style = MaterialTheme.typography.titleLarge)
        Divider(Modifier.padding(vertical = 8.dp))

        // Use a Box to make the entire OutlinedTextField area clickable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDatePicker(true) } // Requests the dialog to be shown
        ) {
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                label = { Text("Work Date") },
                readOnly = true,
                // Ensure it's not focusable to prevent soft keyboard showing
                singleLine = true,
                enabled = false, // Set to false to remove tap ripple but still show value
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Date") }
            )
        }
    }
}