package com.example.airdry.ui.screens.settings

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.theme.Typography
import com.example.airdry.ui.viewmodels.StackViewModel


@Composable
fun ManageStacksScreen(
    viewModel:StackViewModel,
    navController: NavController,
    ) {
    val allStacks by viewModel.allStack.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getAllStacks()
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            "Current Stacks",
            style = Typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        GreenDivider()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
                .border(
                    BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            item {
                HeaderRow()
            }
            items(allStacks) { stack ->
                StackItem(stack , onClick = {
                    navController.navigate(Screen.EditStack.withArgs(stack.stack.id.toString()))
                })
            }
        }
    }
}
@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableText("Type", true)
        TableText("Height", true)
        TableText("Length", true)
        TableText("Thickness", true)
        TableText("# Pieces", true)
        TableText("Cubes", true)
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun StackItem(stack: NestedStack? , onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(0.5.dp, Color.LightGray))
            .clickable{
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableText("${stack?.stack?.displayName} -${stack?.stackCode?.displayName} -${stack?.stackCode?.displayCode}",onClick = {onClick()})
        TableText(stack?.stack?.height.toString(),onClick = {onClick()})
        TableText(stack?.stack?.length.toString(),onClick = {onClick()})
        TableText(stack?.stack?.thickness.toString(),onClick = {onClick()})
        TableText(stack?.stack?.numberOfPieces.toString(),onClick = {onClick()})
        TableText(String.format("%.3f", stack?.stack?.volume ?: 0.0),onClick = {onClick()})
    }
}

@Composable
fun TableText(text: String, isHeader: Boolean = false, onClick: () -> Unit ={}) {
    Text(
        text = text,
        modifier = Modifier
            .padding(horizontal = 4.dp),
        style = if (isHeader)
            MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        else
            MaterialTheme.typography.bodyMedium,
        maxLines = 1
    )
}




