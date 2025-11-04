package com.example.airdry.ui.screens.auth

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.airdry.auth.SessionManager
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.viewmodels.ProfileUiEvent
import com.example.airdry.ui.viewmodels.ProfileViewModel
import com.example.airdry.util.UtilityFunctions.Companion.toUUIDOrNull
import com.example.airdry.util.operational_states.LoggedInState
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController = rememberNavController(),
    sessionManager:SessionManager
) {
    val localContext = LocalContext.current
    val loggedInState by viewModel.loggedInState.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val scope = rememberCoroutineScope()

    val navigateToLogin = { navController.navigate(Screen.Login.route) }

    // --- State and Effect Handling (as per your original logic) ---
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ProfileUiEvent.ShowToast -> {
                    Toast.makeText(localContext, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    LaunchedEffect(loggedInState) {
        if (loggedInState == LoggedInState.LoggedIn) {
            val userIdString = sessionManager.userId
            val userIdUuid = userIdString.toUUIDOrNull()
            if (userIdUuid != null) {
                viewModel.loadProfile(userIdUuid)
            } else {
                Toast.makeText(localContext, "User  not available.", Toast.LENGTH_SHORT).show()
               if(loggedInState == LoggedInState.LoggedOutServerActive)
               {Toast.makeText(localContext, "Not Logged In (No Server)", Toast.LENGTH_SHORT).show()
                   navigateToLogin()}
               if(loggedInState == LoggedInState.LoggedOutNoServer)
               {
                   Toast.makeText(localContext, "Sorry, server unavailable and you logged out", Toast.LENGTH_LONG).show()
                   navController.popBackStack()}
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (loggedInState == LoggedInState.Loading || loggedInState == LoggedInState.Idle) {
            CircularProgressIndicator(Modifier.padding(top = 64.dp))
            Text(
                "Checking status...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        else if (loggedInState == LoggedInState.LoggedIn && profile != null) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileAvatar(profile = profile)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "${profile!!.name} ${profile!!.surname}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Email (Secondary Detail)
                    Text(
                        text = profile!!.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(Modifier.padding(vertical = 16.dp))

                    // Organization & Department Details
                    ProfileDetailRow(label = "Organization", value = profile!!.organization)
                    ProfileDetailRow(label = "Department", value = profile!!.department)
                    ProfileDetailRow(label = "Section", value = profile!!.section)
                    ProfileDetailRow(label = "Role", value = profile!!.userType)
                    HorizontalDivider(Modifier.padding(vertical = 16.dp))

                    // ID
                    Text(
                        text = "User ID: ${profile!!.id.toString().substringBefore('-')}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Log Out Button
            Button(
                onClick = {
                    scope.launch {
                        viewModel.logout()
                        navigateToLogin()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("LOG OUT")
            }
        }
        // 3. Other States
        else {
            Text(
                "Session is inactive. Please log in.",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 64.dp)
            )
            Button(
                onClick = {
                    scope.launch {
                        navigateToLogin()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("LOG IN")
            }
        }
    }
}

// --- Helper Composable for Avatar ---
@Composable
fun ProfileAvatar(profile: Profile?) {
    val imageSize = 100.dp

    // Check if a logo ByteArray exists and is valid
    val logoBitmap = profile?.logo?.let { byteArray ->
        runCatching {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }.getOrNull()
    }

    Surface(
        shape = CircleShape,
        modifier = Modifier.size(imageSize),
        // Use primary color for the border/surface outline
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        if (logoBitmap != null) {
            // Display the actual logo if available
            Image(
                bitmap = logoBitmap.asImageBitmap(),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Display a default icon placeholder
            Icon(
                painter = rememberVectorPainter(Icons.Default.Person),
                contentDescription = "No Avatar",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

// --- Helper Composable for Detail Rows ---
@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}