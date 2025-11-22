package com.example.airdry.ui.screens.workers

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.example.airdry.ui.viewmodels.EmployeeViewModel
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
        nationalId = null,
        isApproved = null,
        createdAt = null,
        updatedAt = null,
        createdBy = null,
        updatedBy = null,
        userTypeId =null,
        organizationId = null,
        departmentId = null,
        sectionId = null,
        email = ""
    ),
    onSubmit: (AppUser) -> Unit,
    viewModel : EmployeeViewModel,
    isEditMode: Boolean = false
) {
    var name by remember { mutableStateOf(worker.name) }
    var surname by remember { mutableStateOf(worker.surname ?: "") }
    var nationalId by remember { mutableStateOf(worker.nationalId ?: "") }
    var phoneNumber by remember { mutableStateOf(worker.phoneNumber) }
    var isActive by remember { mutableStateOf(worker.isActive) }
    var email by remember { mutableStateOf(worker.email)}
    var userTypeId  by remember {mutableStateOf(worker.userTypeId)}
    var departmentId by remember { mutableStateOf(worker.departmentId)}
    var sectionId by remember { mutableStateOf(worker.sectionId) }
    val organizationId =  viewModel.organization.collectAsState().value?.id

    val isNationalIdValid = nationalId?.length == 13 && nationalId?.all { it.isDigit() } == true
    val showIdError = nationalId.isNotEmpty() && !isNationalIdValid
    val isPhoneNumberValid = phoneNumber!!.length == 8 && phoneNumber!!.all { it.isDigit() }
    val isEmailValid  = email.isNotEmpty()
    val isSectionValid = sectionId != null
    val isDepartmentValid = departmentId != null
    val showPhoneNumberError = phoneNumber!!.isNotEmpty() && !isPhoneNumberValid
    val showEmailError = email.isEmpty()

    val context  = LocalContext.current

    val userTypes by viewModel.useTypes.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val sections by viewModel.sections.collectAsState()

    var expandedUserType by remember { mutableStateOf(false) }
    var expandedDepartment by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf(false) }

    val selectedUserType by remember(userTypeId, userTypes) {
        derivedStateOf { userTypes.find { it.id == userTypeId } }
    }
    val selectedDepartment by remember(departmentId, departments) {
        derivedStateOf { departments.find { it.id == departmentId }}
    }
    val selectedSection by remember(sectionId, sections) {
        derivedStateOf { sections.find { it.id == sectionId }}
    }
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        viewModel.populateDropDowns()
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = name!!,
            onValueChange = { name = it.trim() },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
          readOnly =isEditMode
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it.trim() },
            label = { Text("Surname") },
            modifier = Modifier.fillMaxWidth(),
          readOnly=isEditMode
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
            modifier = Modifier.fillMaxWidth(),
          readOnly=isEditMode
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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
          readOnly=isEditMode
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email.toString(),
            isError = showEmailError,
            onValueChange = {
                if (it.isNotEmpty()){
                    email = it
                }},
            label = { Text("Email") },
            supportingText = {
                if (showEmailError) Text("email must not be empty")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
          readOnly=isEditMode
        )

        Spacer(Modifier.height(8.dp))
        Column{

            DropdownFields(       selectedUserType, selectedDepartment, selectedSection,
                expandedUserType, { expandedUserType = it },
                expandedDepartment, { expandedDepartment = it },
                expandedSection, { expandedSection = it },
                userTypes, departments, sections, {
                   userTypeId = it
                }, { departmentId=it
                    sectionId = null
                    viewModel.reloadSectionsByDepartment(it)
                   },
                {sectionId = it},
                isEditMode)


        }
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Is Active", modifier = Modifier.weight(1f))
            Switch(
                checked = isActive == true,
                onCheckedChange = { isActive = it },
              enabled = !isEditMode
            )
        }
        Spacer(Modifier.height(8.dp))
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (isNationalIdValid && isPhoneNumberValid
                    &&isEmailValid){
                onSubmit(
                    AppUser(
                        id = worker.id,
                        name = name,
                        surname = if (surname.isBlank()) null else surname,
                        phoneNumber = phoneNumber,
                        createdAt = worker.createdAt,
                        nationalId =nationalId,
                        isActive = isActive,
                        isApproved = false,
                        updatedAt = null,
                        isPendingInsert =true,
                        createdBy = null,
                        updatedBy = null,
                        userTypeId = userTypeId,
                        organizationId = organizationId,
                        departmentId = departmentId,
                        sectionId = sectionId,
                        email= email
                    )
                )
            } else{
                    Toast.makeText(context, "Invalid National ID or Phone Number", Toast.LENGTH_SHORT).show()
            }
                      },
            enabled = isNationalIdValid && isPhoneNumberValid && isEmailValid &&isSectionValid && isDepartmentValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if(!isEditMode)
                "Save"
            else
                "Exit")
        }
    }

}
