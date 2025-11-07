package com.example.airdry.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.Stack.StackCode
import java.util.UUID

@Composable
fun StackForm(
    title: String,
    stack: Stack = Stack(
        id = UUID.randomUUID(),
        length = 0.0f,
        height = 0.0f,
        thickness = 0.0f,
        numberOfPieces = 0,
        displayName = "",
        stackCodeId =UUID.randomUUID() ,
        volume = 0.0 ,
    ),
    stackCode: StackCode?,
    onSubmit: () -> Unit
)
{

    var length by remember { mutableStateOf(stack.length)}
    var thickness by remember { mutableFloatStateOf(stack.thickness) }
    var volume by remember { mutableDoubleStateOf(stack.volume?.toDouble()?:0.0)}
    var height by remember { mutableFloatStateOf(stack.height)}
    var numberOfPieces by remember { mutableIntStateOf(stack.numberOfPieces)}
    var displayName by remember { mutableStateOf(stack.displayName)}
    var stackDisplayCode  by remember { mutableStateOf(stackCode?.displayCode)}
    var stackDisplay  by remember { mutableStateOf<String>(stackCode?.displayName.toString())}


    Column(Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = "name : $displayName  code : $stackDisplayCode description : $stackDisplay " ,
            onValueChange = {},
            label = { Text("Stack name") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = length.toString(),
            onValueChange = {},
            label = { Text("length") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = thickness.toString(),
            onValueChange = { },
            label = { Text("thickness") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),

        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = height.toString(),
            onValueChange = { },
            label = { Text("height") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),

        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = numberOfPieces.toString(),
            onValueChange = { },
            label = { Text("number of pieces") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = volume.toString(),
            onValueChange = {},
            label = { Text("Cubes") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),

        )
        Spacer(Modifier.height(8.dp))
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {onSubmit()},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exit")
        }
    }
}