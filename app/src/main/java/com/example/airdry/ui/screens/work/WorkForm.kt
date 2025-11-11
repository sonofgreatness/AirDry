package com.example.airdry.ui.screens.work

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.viewmodels.RecordWorkViewModel
import com.example.airdry.ui.viewmodels.WorkFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkForm(
    viewModel: RecordWorkViewModel, // Use the shared ViewModel
    onSubmit: (WorkFormState) -> Unit,
    onDelete: (() -> Unit)? = null, // Only available in Edit mode
    isEditMode: Boolean,
    navController: NavController
) {
    // Collect the state from the ViewModel
    val formState by viewModel.formState.collectAsState()
    val filteredEmployees by viewModel.filteredEmployees.observeAsState(emptyList())
    val focusManager = LocalFocusManager.current
    // --- Local UI States ---
    var showDatePicker by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }

    val context =LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    // Initial Load
    LaunchedEffect(Unit) {
        viewModel.populateStacksAndWorkerLists()
    }

    // Use a Surface or Column as the main container
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Custom Top Bar (Always visible) ---
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Work Entry" else "Record New Work") },
                actions = {
                    if (isEditMode && onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Work")
                        }
                    }
                },
                navigationIcon = {
                    // Example: IconButton(onClick = { navigator.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )

            // --- Core Form Content (Scrollable, takes up remaining space) ---
            // The Modifier.weight(1f) ensures this LazyColumn fills the space between the top and bottom bar
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    // 1. Detect taps anywhere in the LazyColumn area
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            // 2. Clear focus when a tap occurs, dismissing the keyboard
                            focusManager.clearFocus()
                        })
                    },
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp) ) {
                item {
                    WorkDetailsSection(
                        formState = formState,
                        onUpdateDate = { viewModel.updateWorkDate(it) },
                        onShowDatePicker = { showDatePicker = it }
                    )
                }
                item {
                    WorkersSelectionSection(
                        formState = formState,
                        filteredEmployees = filteredEmployees,
                        onSearch = { viewModel.updateFilteredEmployees(it) },
                        onToggleUser = { user -> viewModel.toggleUserSelection(user) }
                    )
                }

                // 3. Stacks Selection Section (Can be repeated, requires volume)
                item {
                    StacksSelectionSection(
                        formState = formState,
                        viewModel,
                        onAddStack = { stack -> viewModel.addStackSelection(stack) },
                        onRemoveStack = { item -> viewModel.removeStackSelection(item) }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface) // Important: Add background to cover content below
                    .padding(16.dp)
                    .imePadding(), // Handles keyboard pushing the content up
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showAlertDialog =true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formState.selectedUsers.isNotEmpty() && formState.selectedStacks.isNotEmpty()
                ) {
                    Text(if (isEditMode) "Save Changes" else "Submit Work")
                }
            }
        }
    }
    if (showDatePicker) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = formState.workDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            viewModel.updateWorkDate(millis)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
    if (showAlertDialog){
        WorkConfirmationDialog(
            onConfirmSave =  {
                onSubmit(formState)
                             showAlertDialog = false
                viewModel.clearFormState()
                navController.navigate(Screen.SelectEmployee.route)
                             },
            onDismiss = {showAlertDialog = false},
            state = formState
        )
    }

}