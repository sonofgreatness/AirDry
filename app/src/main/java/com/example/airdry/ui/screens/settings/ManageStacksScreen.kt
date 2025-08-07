package com.example.airdry.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.database.Entities.Stack
import com.example.airdry.ui.nav.Screen


@Composable
fun ManageStacksScreen(
    allStacks: List<Stack?>,
    navController: NavController,

) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            "Current Stacks",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurface
        )


        GreenDivider()
        Button(
            onClick = { navController.navigate(Screen.CreateStack.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New Stack")
        }
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
                    navController.navigate(Screen.EditStack.withArgs(stack?.id.toString()))
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
        TableText("Breadth", true)
        TableText("Width", true)
        TableText("# Pieces", true)
        TableText("Cubes", true)
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun StackItem(stack: Stack? , onClick: () -> Unit) {
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
        TableText(stack?.type ?: "N/A",onClick = {onClick()})
        TableText(stack?.height.toString(),onClick = {onClick()})
        TableText(stack?.breadth.toString(),onClick = {onClick()})
        TableText(stack?.width.toString(),onClick = {onClick()})
        TableText(stack?.numberOfPieces.toString(),onClick = {onClick()})
        TableText(String.format("%.3f", stack?.getStackCubes() ?: 0.0),onClick = {onClick()})
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




