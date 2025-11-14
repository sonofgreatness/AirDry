package com.example.airdry.ui.screens.work

import android.net.http.SslCertificate.saveState
import android.widget.Toast
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.viewmodels.RecordWorkViewModel
import com.example.airdry.ui.viewmodels.WorkFormState
const val WORKERS_INDEX = 1
const val STACKS_INDEX = 2
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkForm(
    viewModel: RecordWorkViewModel,
    onSubmit: (WorkFormState) -> Unit,
    onDelete: (() -> Unit)? = null,
    isEditMode: Boolean,
    navController: NavController
) {
    val formState by viewModel.formState.collectAsState()
    val updateState by viewModel.updateWorkDone.collectAsState()
    val filteredEmployees by viewModel.filteredEmployees.observeAsState(emptyList())
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 3 }) // 3 pages

    // Scroll states for each page
    val scrollState0 = rememberScrollState()
    val scrollState1 = rememberScrollState()
    val scrollState2 = rememberScrollState()

    // Side effects
    LaunchedEffect(Unit) { viewModel.populateStacksAndWorkerLists() }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Top AppBar ---
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Work Entry" else "Record New Work") },
                actions = {
                    if (isEditMode && onDelete != null && updateState?.isPendingInsert == true) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Work")
                        }
                    }
                }
            )

            // --- Pager Section ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() } }
            ) {
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapAnimationSpec = spring()
                    )
                ) { page ->
                    when (page) {
                        0 -> WorkDetailsSection(
                            formState,
                            onUpdateDate = { viewModel.updateWorkDate(it) },
                            onShowDatePicker = { showDatePicker = it }
                        )
                        1 -> WorkersSelectionSection(
                            formState,
                            filteredEmployees,
                            onSearch = { viewModel.updateFilteredEmployees(it) },
                            onToggleUser = { viewModel.toggleUserSelection(it) }
                        )
                        2 -> StacksSelectionSection(
                            formState,
                            viewModel,
                            onAddStack = { viewModel.addStackSelection(it) },
                            onRemoveStack = { viewModel.removeStackSelection(it) }
                        )
                    }
                }

                // --- Scroll hint ---
                val scrollState = when (pagerState.currentPage) {
                    0 -> scrollState0
                    1 -> scrollState1
                    2 -> scrollState2
                    else -> null
                }

                scrollState?.let {
                    if (it.value < it.maxValue) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                        )
                                    )
                                )
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Scroll down",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            }

            // --- Bottom Submit Button ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
                    .imePadding(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showAlertDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formState.selectedUsers.isNotEmpty() && formState.selectedStacks.isNotEmpty()
                ) {
                    Text(if (isEditMode) "Save Changes" else "Submit Work")
                }
            }
        }
    }

    // --- Date Picker ---
    if (showDatePicker) {
        val dateState = rememberDatePickerState(initialSelectedDateMillis = formState.workDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { viewModel.updateWorkDate(it) }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = dateState) }
    }

    // --- Confirmation Dialog ---
    if (showAlertDialog) {
        WorkConfirmationDialog(
            onConfirmSave = {
                onSubmit(formState)
                showAlertDialog = false
                viewModel.clearFormState()
                navController.navigate(Screen.SelectEmployee.route)
            },
            onDismiss = { showAlertDialog = false },
            state = formState
        )
    }
}
