package com.example.airdry.ui.screens.work

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Stack
import com.example.airdry.ui.viewmodels.WorkViewModel
import com.example.airdry.util.Enums

@Composable
fun StackFormScreen(viewModel: WorkViewModel
, onStackCreated: (Stack) -> Unit) {

    var selectedLength by remember { mutableStateOf(Enums.StackLength.L1_8) }
    var selectedWidth by remember { mutableStateOf(Enums.StackWidth.WIDE) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Stack Length")
        Enums.StackLength.values().forEach { length ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedLength == length,
                    onClick = { selectedLength = length }
                )
                Text(length.name)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Stack Width")
        Enums.StackWidth.values().forEach { width ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedWidth == width,
                    onClick = { selectedWidth = width }
                )
                Text(width.name)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.createStack(selectedLength, selectedWidth, onStackCreated)
        }) {
            Text("Save Stack")
        }
    }
}
