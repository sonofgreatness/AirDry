package com.example.airdry.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.airdry.database.Entities.Stack
import com.example.airdry.ui.theme.ForestGreen
import java.sql.Timestamp
import java.util.UUID

@Composable
fun StackForm(
    title : String,
    stack : Stack = Stack(
        id = UUID.randomUUID(),
        type = "new type name ",
        breadth = 0.0f,
        width = 0.0f,
        height = 0.0f,
        numberOfPieces = 0,
        dateCreated =Timestamp(System.currentTimeMillis())
    ),
    onSubmit: (Stack) -> Unit,
    onDelete: (Stack) -> Unit ={},
    showDeleteButton:Boolean =false
)
{

    var type by remember { mutableStateOf(stack.type)}
    var breadth by remember { mutableFloatStateOf(stack.breadth) }
    var width by remember { mutableFloatStateOf(stack.width)}
    var height by remember { mutableFloatStateOf(stack.height)}
    var numberOfPieces by remember { mutableIntStateOf(stack.numberOfPieces)}

    Column(Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = type,
            onValueChange = { type = it.trim() },
            label = { Text("Stack type (name)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = breadth.toString(),
            onValueChange = { breadth = it.toFloat() },
            label = { Text("breadth/length") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = width.toString(),
            onValueChange = { width = it.toFloat() },
            label = { Text("width") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
        )



        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = height.toString(),
            onValueChange = { height = it.toFloat() },
            label = { Text("height") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
        )


        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = numberOfPieces.toString(),
            onValueChange = { numberOfPieces = it.toInt() },
            label = { Text("number of pieces") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )





        Spacer(Modifier.height(8.dp))


        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onSubmit(
                    Stack(
                        id = stack.id,
                        type = type,
                        breadth = breadth,
                        width = width,
                        height = height,
                        numberOfPieces = numberOfPieces,
                    )
                )

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        if (showDeleteButton){
            Spacer(Modifier.height(16.dp))
            Button(
            onClick = { onDelete(stack) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Delete")
        }

    }

    }

}