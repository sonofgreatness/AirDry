package com.example.airdry.ui.screens.work

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.ui.viewmodels.RecordWorkViewModel
import com.example.airdry.ui.viewmodels.SelectedStackItem
import com.example.airdry.ui.viewmodels.WorkFormState

@Composable
fun StacksSelectionSection(
    formState: WorkFormState,
     viewModel: RecordWorkViewModel,
    onAddStack: (NestedStack) -> Unit,
    onRemoveStack: (SelectedStackItem) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedStack by remember { mutableStateOf<NestedStack?>(null) }
    val filteredStacks: List<NestedStack>  by viewModel.filteredStacks.observeAsState(emptyList())
    val totalVolume = formState.selectedStacks.sumOf { it.stack.stack.volume ?: 0.0 }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "2. Select Stacks (${formState.selectedStacks.size} entries | ${String.format("%.2f m³", totalVolume)})",
            style = MaterialTheme.typography.titleLarge
        )
        VerticalDivider(Modifier.padding(vertical = 8.dp))

        // --- Current Selection Block (Dropdown + Volume) ---
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Dropdown for Stack Selection

                TextField(value =searchText,
                    placeholder = {Text("Search...")},
                    onValueChange = {
                        searchText = it
                        viewModel.updateFilteredStacks(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1

                )
                Spacer(Modifier.height(8.dp))

                StackDropdown(
                    filteredStacks = filteredStacks,
                    selectedStack = selectedStack,
                    searchText = searchText,
                    onSelect = { selectedStack = it }
                )
                // Add Button
                Button(
                    onClick = {
                        selectedStack?.let { stack ->
                            onAddStack(stack)
                            selectedStack = null
                        }
                    },
                    enabled = selectedStack != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Stack Entry")
                }
            }
        }

        // --- Selected Stacks Preview ---
        if (formState.selectedStacks.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Stack Entries Preview:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                formState.selectedStacks.forEach { item ->
                    StackPreviewRow(item = item, onRemove = { onRemoveStack(item) })
                }
            }
        }
    }
}