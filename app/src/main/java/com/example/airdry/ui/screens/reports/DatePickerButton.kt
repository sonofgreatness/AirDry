package com.example.airdry.ui.screens.reports

import android.app.DatePickerDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate

@Composable
fun DatePickerButton(
    label: String,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current

    val year = selectedDate.year
    val month = selectedDate.monthValue - 1 // zero-based for DatePicker
    val day = selectedDate.dayOfMonth

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, pickedYear, pickedMonth, pickedDay ->
                onDateSelected(LocalDate.of(pickedYear, pickedMonth + 1, pickedDay))
            },
            year,
            month,
            day
        )
    }
    Button(onClick = { datePickerDialog.show() }) {
        Text(label)
    }
}
