package com.example.airdry.ui.screens.work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
    val filteredStacks: List<NestedStack> by viewModel.filteredStacks.observeAsState(emptyList())
    val totalVolume = formState.selectedStacks.sumOf { it.stack.stack.volume ?: 0.0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Page Header ---
        Text(
            "2. Select Stacks (${formState.selectedStacks.size} entries | ${"%.2f".format(totalVolume)} m³)",
            style = MaterialTheme.typography.titleLarge
        )
        Divider(Modifier.padding(vertical = 8.dp))

        // --- Scrollable Content ---
        Column(
            modifier = Modifier
                .weight(1f) // take remaining height
                .verticalScroll(rememberScrollState()), // allow scrolling
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Card + Search + Dropdown + Add Button
            Card(
                Modifier.fillMaxWidth(),

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Column(Modifier.padding(26.dp), verticalArrangement = Arrangement.spacedBy(64.dp)) {
                    TextField(
                        value = searchText,
                        placeholder = { Text("Search...") },
                        onValueChange = {
                            searchText = it
                            viewModel.updateFilteredStacks(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        trailingIcon = {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = "Search",
                                modifier = Modifier.clickable {
                                    searchText = ""
                                    viewModel.updateFilteredStacks(searchText)
                                }
                            )
                        }
                    )

                    StackDropdown(
                        filteredStacks = filteredStacks,
                        selectedStack = selectedStack,
                        searchText = searchText,
                        onSelect = { selectedStack = it }
                    )

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
                Text(
                    "Stack Entries Preview:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                formState.selectedStacks.forEach { item ->
                    StackPreviewRow(item = item, onRemove = { onRemoveStack(item) })
                }
            }
        }
    }
}