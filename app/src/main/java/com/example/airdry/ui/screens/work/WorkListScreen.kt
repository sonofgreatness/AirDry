package com.example.airdry.ui.screens.work

// In package com.example.airdry.ui.screens.work

// ... (Your existing imports) ...
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.airdry.data.dto.Work.StackInfoDTO
import com.example.airdry.data.dto.Work.UserInfoDTO
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.Work.WorkSummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.ui.screens.workers.DropDownFieldsNoUserTypes
import com.example.airdry.ui.screens.workers.DropdownFields
import com.example.airdry.ui.viewmodels.WorkViewModel
import com.example.airdry.util.ConstanstFromShared.Companion.WORK_DONE_EDIT_GRACE_PERIOD
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import com.example.airdry.util.UtilityFunctions
import com.example.airdry.util.operational_states.WorkDataFiltersDTOState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkListScreen(
    viewModel: WorkViewModel,
    navigator: NavController
) {

    val workSummaries by viewModel.dailyWorkSummaries.collectAsState()
    val filtersDTO by viewModel.filterDto.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val sections by viewModel.sections.collectAsState()

    val pageSize = DEFAULT_PAGE_SIZE

    val expandedDays = remember { mutableStateMapOf<String, Boolean>() }
    var showFilterModal by remember { mutableStateOf(false) } // State for modal visibility
    val sheetState = rememberModalBottomSheetState()


    LaunchedEffect(Unit) {
        viewModel.populateDailyWorkSummaries(pageSize = pageSize)
        viewModel.populateDepartmentsAndSections()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

            // --- Header Row with Filter Button ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Work Records",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showFilterModal = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Show Filters")
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }
            Spacer(Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(workSummaries) { daySummary ->
                    WorkDaySummaryCard(
                        summary = daySummary,
                        expanded = expandedDays[daySummary.dayLabel] == true,
                        onToggleExpand = { expanded ->
                            expandedDays[daySummary.dayLabel] = expanded
                        },
                        navigator
                    )
                }
            }
        }

        // --- Filter Modal Implementation ---
        if (showFilterModal) {
            ModalBottomSheet(
                onDismissRequest = { showFilterModal = false },
                sheetState = sheetState
            ) {
                FilterModalContent(
                    filtersDTO = filtersDTO,
                    departments = departments,
                    sections = sections,
                    viewModel = viewModel,
                    onApply = { showFilterModal = false },
                    onClear = { viewModel.clearAllFilters() }
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterModalContent(
    filtersDTO: WorkDataFiltersDTO,
    departments: List<Department>, // Assuming you have Department model
    sections: List<Section>, // Assuming you have Section model
    viewModel: WorkViewModel,
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val openDialog = remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var selectedMe  = filtersDTO.includeOtherStaffRecords

    // Expanded state for dropdowns in the modal
    var expandedDepartment by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf(false) }

    val selectedDepartment by remember(filtersDTO, departments) {
        derivedStateOf { departments.find { it.id == filtersDTO.filterDepartmentId } }
    }
    val selectedSection by remember(filtersDTO, sections) {
        derivedStateOf { sections.find { it.id == filtersDTO.filterSectionId } }
    }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect{
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Title ---
        Text(
            text = "Filter Work Records",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Divider()

        // 1. Date Filter
        OutlinedTextField(
            value = filtersDTO.filterDate ?: "",
            onValueChange = {},
            label = { Text("Filter by Date") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { openDialog.value = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                }
            }
        )
          // Date Picker Dialog
        if (openDialog.value) {
            DatePickerDialog(
                onDismissRequest = { openDialog.value = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dateState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                val formatted = date.format(dateFormatter)
                                viewModel.onChangeFilter(
                                    WorkDataFiltersDTOState.FilterDate,
                                    formatted
                                )
                            }
                            openDialog.value = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog.value = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = dateState, modifier = Modifier.padding(8.dp))
            }
        }

        // 2. Created By Filter (Me / Other Staff Member)
        // Note: The server filters by userId. We'll use filterUserId here.
        Text("Filter by:", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Option 1: Current User (Simulated for filterUserId = CurrentUser's ID)
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.RadioButton(
                    selected = selectedMe, // Assuming non-null means current user filter
                    onClick = {
                        val oldSelectMe = selectedMe
                        viewModel.onChangeFilter(WorkDataFiltersDTOState.FilterForMeOnly(selectedMe),"")
                        selectedMe = !oldSelectMe
                    }
                )
                Text("All Staff Records", style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.RadioButton(
                    selected = !selectedMe,
                    onClick = {
                        val oldSelectedMe = selectedMe
                        viewModel.onChangeFilter(WorkDataFiltersDTOState.FilterForMeOnly(!selectedMe), "")
                        selectedMe = !oldSelectedMe
                    }
                )
                Text("Me Only", style = MaterialTheme.typography.bodyMedium)
            }
        }
        // 3. Department and Section Dropdowns (Using your existing UI style)
        DropDownFieldsNoUserTypes(
            selectedDepartment = selectedDepartment,
            selectedSection = selectedSection,
            expandedUserType = false, // Not used
            onExpandUserType = {},
            expandedDepartment = expandedDepartment,
            onExpandDepartment = { expandedDepartment = it },
            expandedSection = expandedSection,
            onExpandSection = { expandedSection = it },
            departments = departments,
            sections = sections,
            onDepartmentChange = { id ->
                viewModel.onChangeFilter(WorkDataFiltersDTOState.FilterDepartment, id.toString())
            },
            onSectionChange = { id ->
                viewModel.onChangeFilter(WorkDataFiltersDTOState.FilterSection, id.toString())
            }
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Clear Filters")
            }
            Button(
                onClick = onApply,
                modifier = Modifier.weight(1f)
            ) {
                Text("Apply Filters")
            }
        }
    }
}



/** ✅ DAY SUMMARY CARD **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkDaySummaryCard(
    summary: WorkDaySummaryDTO,
    expanded: Boolean,
    onToggleExpand: (Boolean) -> Unit,
    navigator: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(summary.dayLabel, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatInline("Volume (Cubes)", String.format("%.2f m³", summary.totalVolume))
                        StatInline("Stacks", summary.totalStacks.toString())
                        StatInline("Users", summary.totalUsers.toString())
                    }
                }
                TextButton(onClick = { onToggleExpand(!expanded) }) {
                    Text(if (expanded) "Collapse" else "Expand")
                }
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    summary.workList.forEach { work ->
                        WorkSummaryItem(work, navigator = navigator)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}
/** ✅ INLINE STAT DISPLAY **/
@Composable
fun StatInline(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(end = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
    }
}

/** ✅ WORK SUMMARY ITEM — Enhanced UI **/
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WorkSummaryItem(work: WorkSummaryDTO, navigator: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // -------------------------
            // Header: Date & Stats
            // -------------------------
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📅 ${work.workDate}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatInline("📦 Cubes", String.format("%.2f m³", work.totalVolume))
                        StatInline("🗂 Stacks", work.totalStacks.toString())
                        StatInline("👷 Users", work.totalUsers.toString())
                        work.capturedBy?.let { StatInline("✍️ Captured by", it) }
                        work.capturedAt?.let { StatInline("⏱ Captured at", it) }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        work.updatedBy?.let { StatInline("🔄 Updated by", it) }
                        if (work.capturedAt != work.updatedAt) {
                            work.updatedAt?.let { StatInline("⏱ Updated at", it) }
                        }
                    }
                    var showEditButton  = false
                    work.capturedAt.let{ dateString->
                        val updatedDateTime = java.time.LocalDateTime.parse(dateString!!, UtilityFunctions.Companion.customApiFormatterTEXT)
                        val updatedInstant = updatedDateTime
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                        val daysPassed = java.time.Duration.between(updatedInstant,Instant.now()).toDays()
                        showEditButton = daysPassed < WORK_DONE_EDIT_GRACE_PERIOD
                    }
                    if(showEditButton){
                        IconButton(
                            onClick = {
                                //Navigate to edit work
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Work Entry",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // -------------------------
            // Stacks Section
            // -------------------------
            SectionHeader(title = "Stacks Worked", subtitle = "Cubes")
            Column(modifier = Modifier.fillMaxWidth().padding(start = 6.dp)) {
                work.stacks.forEach { StackRow(it) }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // -------------------------
            // Users Section
            // -------------------------
            SectionHeader(title = "Workers", subtitle = "Personal Cubes")
            Column(modifier = Modifier.fillMaxWidth().padding(start = 6.dp)) {
                work.users.forEach { UserRow(it, navigator) }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        Text(subtitle, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
    }
}

/** ✅ STACK ROW **/
@Composable
fun StackRow(stack: StackInfoDTO) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(stack.displayName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            Text("${stack.stackCodeDisplayName} -- ${stack.stackCode}", style = MaterialTheme.typography.labelSmall)
        }
        Text(String.format("%.3f m³", stack.volume), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun UserRow(user: UserInfoDTO, navigator: NavController) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp).clickable{
                // user work
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Left: Avatar Circle with Initials ---
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (user.isActive) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${user.name.firstOrNull() ?: '?'}${user.surname.firstOrNull() ?: '?'}".uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // --- Middle: User info ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.name} ${user.surname}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = user.phoneNumber,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip("Active", user.isActive, Icons.Default.CheckCircle)
                    StatusChip("Approved", user.isApproved, Icons.Default.Verified)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.3f m³", user.personalCubes),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "\uD83D\uDCC8 Cubes",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                )
            }
        }
    }
}

@Composable
fun StatusChip(label: String, status: Boolean, icon: ImageVector) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (status) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (status) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (status) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.outline
            )
        )
    }
}

