package com.example.airdry.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.airdry.R
import com.example.airdry.domain.REST_API.data_class.ReleaseResponse
import com.example.airdry.ui.viewmodels.ConnectionsViewModel


@Composable
fun ManageUpdatesScreen(viewModel: ConnectionsViewModel) {
    val releases by viewModel.releases.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllReleases()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(releases) { index, release ->
            val isLatest = index == 0
            ReleaseItem(
                release = release,
                isLatest = isLatest,
                onDownload = { url -> viewModel.downloadUpdate(url) }
            )
        }
    }
}

@Composable
fun ReleaseItem(
    release: ReleaseResponse,
    isLatest: Boolean,
    onDownload: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = if (isLatest) {
                    painterResource(R.drawable.ic_star) // your drawable
                } else {
                    painterResource(R.drawable.cloud_download)
                },
                contentDescription = null,
                tint = if (isLatest) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = release.tag_name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Published: ${release.published_at.substring(0, 10)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Button(onClick = {
                release.assets.firstOrNull()?.browser_download_url?.let(onDownload)
            }) {
                Text("Download")
            }
        }
    }
}


