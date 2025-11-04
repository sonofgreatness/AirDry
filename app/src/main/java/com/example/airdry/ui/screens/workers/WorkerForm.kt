package com.example.airdry.ui.screens.workers

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.ui.theme.AirDryTheme
import java.util.UUID

@Composable
fun WorkerForm(
    title: String,
    worker: AppUser = AppUser(
        id = UUID.randomUUID(),
        name = "",
        surname = null,
        phoneNumber = "",
        isActive = true,
        secondaryPhone = null,
        isApproved = null,
        createdAt = null,
        updatedAt = null,
        createdBy = null,
        updatedBy = null,
        userTypeId = null,
        organizationId = null,
        departmentId = null,
        sectionId = null
    ),
    onSubmit: (AppUser) -> Unit
) {
    var name by remember { mutableStateOf(worker.name) }
    var surname by remember { mutableStateOf(worker.surname ?: "") }
    var nationalId by remember { mutableStateOf(worker.secondaryPhone) }
    var phoneNumber by remember { mutableStateOf(worker.phoneNumber) }
    var isActive by remember { mutableStateOf(worker.isActive) }
    val isNationalIdValid = nationalId!!.length == 13 && nationalId!!.all { it.isDigit() }
    val showIdError = nationalId!!.isNotEmpty() && !isNationalIdValid
    val isPhoneNumberValid = phoneNumber!!.length == 8 && phoneNumber!!.all { it.isDigit() }
    val showPhoneNumberError = phoneNumber!!.isNotEmpty() && !isPhoneNumberValid
    val context  = LocalContext.current
    Column(Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = name!!,
            onValueChange = { name = it.trim() },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it.trim() },
            label = { Text("Surname") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))



        OutlinedTextField(
            value = nationalId.toString(),
            onValueChange = {
                // Only allow digits and up to 13 characters
                if (it.length <= 13 && it.all { char -> char.isDigit() }) {
                    nationalId = it
                }
            },
            label = { Text("National ID") },
            isError = showIdError,
            supportingText = {
                if (showIdError) Text("National ID must be exactly 13 digits.")
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumber.toString(),
            isError = showPhoneNumberError,
            onValueChange = {
                if (it.length <= 8 && it.all { char -> char.isDigit() }){
                phoneNumber = it
                }},
            label = { Text("Phone Number") },
            supportingText = {
                if (showPhoneNumberError) Text("Phone number must be exactly 8 digits.")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Is Active", modifier = Modifier.weight(1f))
            Switch(
                checked = isActive == true,
                onCheckedChange = { isActive = it }
            )
        }

        Spacer(Modifier.height(8.dp))


        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (isNationalIdValid && isPhoneNumberValid){
                onSubmit(
                    AppUser(
                        id = worker.id,
                        name = name,
                        surname = if (surname.isBlank()) null else surname,
                        phoneNumber = phoneNumber,
                        createdAt = worker.createdAt,
                        secondaryPhone =null,
                        isActive = null,
                        isApproved = null,
                        updatedAt = null,
                        createdBy = null,
                        updatedBy = null,
                        userTypeId = null,
                        organizationId = null,
                        departmentId = null,
                        sectionId = null,
                    )
                )
            } else{
                    Toast.makeText(context, "Invalid National ID or Phone Number", Toast.LENGTH_SHORT).show()
            }
                      },
            enabled = isNationalIdValid && isPhoneNumberValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun WorkerFormPreview() {
    AirDryTheme {
        WorkerForm(title = "Create Worker", onSubmit = {})
    }
}