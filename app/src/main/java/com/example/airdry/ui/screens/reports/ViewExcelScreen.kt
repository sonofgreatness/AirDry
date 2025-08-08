package com.example.airdry.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.airdry.ui.viewmodels.WorkViewModel

@Composable
fun ViewExcelScreen(
    viewModel: WorkViewModel
) {

    val files by viewModel.files.observeAsState(emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadExcelFiles()
    }

    if (files.isEmpty()) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No reports found", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(2), // 2 rows, scrolls horizontally
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(files) { item ->
                ExcelFileCard(
                    fileName = item.name,
                    onOpen = { viewModel.openFile(context, item) },
                    onShare = { viewModel.shareFile(context, item) },
                    onDelete = { viewModel.deleteFile(item) }
                )
            }
        }
    }
}