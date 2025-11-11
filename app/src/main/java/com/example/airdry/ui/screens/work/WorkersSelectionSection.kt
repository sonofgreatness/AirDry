package com.example.airdry.ui.screens.work

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.ui.viewmodels.WorkFormState

@Composable
fun WorkersSelectionSection(
    formState: WorkFormState,
    filteredEmployees: List<NestedAppUser>,
    onSearch: (String) -> Unit,
    onToggleUser: (NestedAppUser) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("1. Select Workers (${formState.selectedUsers.size} selected)", style = MaterialTheme.typography.titleLarge)
        Divider(Modifier.padding(vertical = 8.dp))

        // --- Search/Filter Input ---
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                onSearch(it)
            },
            label = { Text("Search Employee Name") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = { if(searchText.isNotEmpty()) IconButton(onClick = { searchText = ""; onSearch("") }) { Icon(Icons.Default.Clear, "Clear") } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // --- Filtered Results List ---
        Text("Available Workers:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        Card(
            Modifier.fillMaxWidth().heightIn(max = 200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            LazyColumn(Modifier.fillMaxWidth()) {
                items(filteredEmployees, key = { it.appUser.id }) { employee ->
                    val isSelected = formState.selectedUsers.any { it.appUser.id == employee.appUser.id }
                    UserSelectRow(
                        employee = employee,
                        isSelected = isSelected,
                        onClick = { onToggleUser(employee) }
                    )
                    Divider(Modifier.fillMaxWidth().height(1.dp))
                }
            }
        }

        // --- Selected Users Preview ---
        if (formState.selectedUsers.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Selected Workers Preview:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                formState.selectedUsers.forEach { user ->
                    UserPreviewRow(user = user, onRemove = { onToggleUser(user) })
                }
            }
        }
    }
}