package com.example.airdry.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airdry.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onClose: (() -> Unit)? = null // optional close action if shown as dialog/sheet
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val clipboard = LocalClipboardManager.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            // Top App Bar
            TopAppBar(
                title = {
                    Column {
                        Text("About AirDry", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Accurate and timely recording of production work", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Purpose card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painterResource(R.drawable.info_24), contentDescription = "Purpose", modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Purpose", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }

                        Text(
                            "AirDry helps the production department accurately and timely record work done every day. " +
                                    "It centralizes personnel information and lets workers record stacks completed at the end of each working day.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                        Text("Key features", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))

                        FeatureBullet("Personnel management — keep employee records and assignments.")
                        FeatureBullet("Stack recording — record stacks (pieces/cubes) done by each worker daily.")
                        FeatureBullet("Stack Management settings — configure stack types: name, pieces per stack, etc.")
                        FeatureBullet("Report generation — creates multi-sheet Excel workbooks for analysis.")
                    }
                }

                // Reports explanation
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.summarize_24), contentDescription = "Reports", modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Reports (Excel)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }

                        Text(
                            "When you export reports the app generates an Excel workbook with three sheets:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(6.dp))

                        // Sheets list
                        SheetBullet("Raw Data — all stacks by all personnel in a selected period.")
                        SheetBullet("Daily Summary — number of cubes per person per day + totals.")
                        SheetBullet("Period Summary — total cubes and stacks per person across the selected period.")

                        Spacer(Modifier.height(6.dp))
                        Text("These reports help operations and management review individual and team productivity over time.", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // How to use (instructions)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.description_24), contentDescription = "How to use", modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("How to use", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }

                        Text("1. Go to Settings → Stack Management to define stack types (name and pieces).", style = MaterialTheme.typography.bodyMedium)
                        Text("2. During each shift, workers record completed stacks using the Work Done screen.", style = MaterialTheme.typography.bodyMedium)
                        Text("3. At period end, export reports to review raw and summarized output.", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Contact card with actionable phone and email
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = "Contact", modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Contact & Support", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }

                        Text(
                            "If you need help configuring the app or understanding reports I'm available for clarifications.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(6.dp))

                        // Phone and Email actions
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionButton(
                                label = "+268 769 11464",
                                icon = Icons.Default.Call,
                                onClick = { openDialer(context, "+26876911464") },
                                modifier = Modifier.weight(1f)
                            )
                            ActionButton(
                                label = "+268 794 87461",
                                icon = Icons.Default.Call,
                                onClick = { openDialer(context, "+26879487461") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            ActionButton(
                                label = "Email: simphiweiq@gmail.com",
                                icon = Icons.Default.Email,
                                onClick = { openEmail(context, "simphiweiq@gmail.com", subject = "AirDry support") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                clipboard.setText(AnnotatedString("+26876911464, +26879487461"))
                            }) {
                                Icon(painterResource(R.drawable.copy_all_24), contentDescription = "Copy contact")
                            }
                        }
                    }
                }

                // License / Terms
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "License", modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("User License & Restrictions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }


                        SelectionContainer {
                            Text(
                                "This application is the intellectual property of the author. " +
                                        "Use of this application in production environments without the permission or knowledge of the author is prohibited. " +
                                        "Contact the author for licensing, customization, or commercial use.",
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Text("If you require explicit permission, contact the author using the phone numbers or email above.", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(Modifier.height(36.dp)) // bottom padding
            }
        }
    }
}

@Composable
private fun FeatureBullet(text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Text("•", modifier = Modifier.padding(end = 8.dp), fontSize = 18.sp)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SheetBullet(text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Icon(
           painterResource(R.drawable.summarize_24),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))

        Text(text, style = MaterialTheme.typography.bodyMedium, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {

    Button(onClick = onClick, modifier = modifier.height(44.dp)) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private fun openDialer(context: Context, phoneNumber: String) {
    val uri = Uri.parse("tel:$phoneNumber")
    val intent = Intent(Intent.ACTION_DIAL, uri)
    // FLAG_ACTIVITY_NEW_TASK not required for activities launched from activity context; safe to add if calling from non-activity
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private fun openEmail(context: Context, email: String, subject: String = "") {
    val uri = Uri.parse("mailto:$email?subject=${Uri.encode(subject)}")
    val intent = Intent(Intent.ACTION_SENDTO, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}