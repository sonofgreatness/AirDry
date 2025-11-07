package com.example.airdry.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.viewmodels.StackViewModel
import java.util.Locale

@Composable
fun ManageStacksScreen(
    viewModel: StackViewModel,
    navController: NavController,
) {
    // ... (Existing code for viewModel, LaunchedEffects, Context) ...
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
            "Stack Library", // New title
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        GreenDivider() // Assuming you have this composable

        // 👇 Using LazyVerticalGrid for a "gallery" or "library shelf" look
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Two cards per row, like an open book
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed (allStacks) { index,stack ->
                StackDetailsCard(stack = stack, onClick = {
                    navController.navigate(Screen.EditStack.withArgs(stack.stack.id.toString()))
                })
            }
        }
    }
}
@Composable
fun StackDetailsCard(stack: NestedStack, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Card is clickable
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Primary Identification (The "Title" of the Book)
            Text(
                text = stack.stack.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Secondary Identification (The "Subtitle" or "Edition")
            Text(
                text = "${stack.stackCode.displayName} (${stack.stackCode.displayCode})",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            // --- Divider for visual break ---
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            // Key Metrics - Presented as a comfortable, readable list
            KeyMetricRow(label = "Pieces", value = stack.stack.numberOfPieces.toString())
            KeyMetricRow(label = "Cubes (m³)", value = String.format(Locale.getDefault(), "%.3f", stack.stack.volume ?: 0.0))

            Spacer(modifier = Modifier.height(8.dp))

            // Dimensions - Grouped together
            DimensionRow(
                label = "Dims",
                height = stack.stack.height.toString(),
                length = stack.stack.length.toString(),
                thickness = stack.stack.thickness.toString()
            )
        }
    }
}
@Composable
fun KeyMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DimensionRow(label: String, height: String, length: String, thickness: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label (H x L x T)",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = "$height x $length x $thickness",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}