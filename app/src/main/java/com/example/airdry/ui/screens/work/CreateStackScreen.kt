package com.example.airdry.ui.screens.work

import androidx.compose.foundation.layout.Column
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
import com.example.airdry.database.Entities.Stack
import com.example.airdry.util.Enums

@Composable
fun CreateStackScreen(
    onStackCreated: (Stack) -> Unit
) {
    var selectedLength by remember { mutableStateOf(Enums.StackLength.L1_8) }
    var selectedWidth by remember { mutableStateOf(Enums.StackWidth.WIDE) }

    Column(Modifier.padding(16.dp)) {
        Text("Create Stack", style = MaterialTheme.typography.titleMedium)

        DropdownMenuBox(
            items = Enums.StackLength.values().toList(),
            selectedItem = selectedLength,
            onItemSelected = { selectedLength = it },
            label = { it.name }
        )

        DropdownMenuBox(
            items = Enums.StackWidth.values().toList(),
            selectedItem = selectedWidth,
            onItemSelected = { selectedWidth = it },
            label = { it.name }
        )

        Button(onClick = {
            val newStack = Stack(
                length = selectedLength,
                width = selectedWidth
            )
            onStackCreated(newStack)
        }) {
            Text("Add Stack")
        }
    }
}
