package com.example.airdry.ui.screens.work


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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.airdry.data.dto.Work.StackInfoDTO
import com.example.airdry.data.dto.Work.UserInfoDTO
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.Work.WorkSummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.ui.nav.Screen
import com.example.airdry.ui.screens.workers.DropDownFieldsNoUserTypes
import com.example.airdry.ui.viewmodels.WorkViewModel
import com.example.airdry.util.ConstanstFromShared.Companion.WORK_DONE_EDIT_GRACE_PERIOD_CAPTURER
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import com.example.airdry.util.Constants.Companion.FILTER_DATE_FORMAT
import com.example.airdry.util.UtilityFunctions
import com.example.airdry.util.UtilityFunctions.Companion.covertNumberTo2DecimalString
import com.example.airdry.util.UtilityFunctions.Companion.covertNumberTo3DecimalString
import com.example.airdry.util.UtilityFunctions.Companion.extractFullNameFromApiString
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


    val workSummaries  by  viewModel.dailyWorkSummaries.collectAsState()
    val filtersDTO by viewModel.filterDto.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val sections by viewModel.sections.collectAsState()

    val totalPages by viewModel.totalPages.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val isMoreDataAvailable = currentPage < totalPages
    val pageSize = DEFAULT_PAGE_SIZE
    var showSettingsMenu by remember {mutableStateOf(false)}

    val expandedDays = remember {mutableStateMapOf<String,Boolean>() }
    var showFilterModal by remember {mutableStateOf(false)}
    val sheetState = rememberModalBottomSheetState()
    val viewOnlyMode by  viewModel.viewModeOnly.collectAsState()
    val isSyncing   by viewModel.isSyncing.collectAsState()
    val users by  viewModel.users.observeAsState()
    var expandedUser by  remember {mutableStateOf(true)}
    var showWorkerDropDown by remember {mutableStateOf(false)}
    val selectedUser by remember(filtersDTO, users) {
        derivedStateOf { users?.find { it.id == filtersDTO.filterUserId } }
    }
    val listState = rememberLazyListState()


    LaunchedEffect(Unit) {
        viewModel.refreshData()
        viewModel.populateDepartmentsAndSections()
        viewModel.loadAllUsers()
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Work Records",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .size(34.dp)
                            .padding(8.dp)
                            .clickable {
                                navigator.navigate(Screen.RecordWork.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Record New Work",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { showFilterModal = true }) {
                        Icon(Icons.Default.FilterList,
                            contentDescription = "Show Filters",
                            tint =  MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(8.dp))
                    Box {
                        IconButton(onClick = { showSettingsMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showSettingsMenu,
                            onDismissRequest = { showSettingsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("View-Only Mode")
                                        Switch(
                                            checked = viewOnlyMode,
                                            onCheckedChange = { isChecked ->
                                                viewModel.setViewMode(isChecked)
                                            }
                                        )
                                    }
                                },
                                onClick = {
                                    viewModel.setViewMode(!viewOnlyMode)
                                    viewModel.refreshData()
                                    showSettingsMenu = false

                                }
                            )
                        }
                    }
                }
            }
            if(showWorkerDropDown){
                UserDropDown(
                    expandedUser = expandedUser,
                    onExpandedChange = { expandedUser = it },
                    selectedUser = selectedUser,
                    users = users,
                    viewModel = viewModel)
            }
            Spacer(Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
                ){
                Icon(
                    if (showWorkerDropDown)
                      Icons.Default.ExpandLess
                    else
                        Icons.Default.ExpandMore
                    , contentDescription = "Opens worker Select"
                    , modifier = Modifier.size(36.dp).clickable{
                        showWorkerDropDown =!showWorkerDropDown
                        if (!showWorkerDropDown){
                            viewModel.clearAllFilters()
                        }

                    }
                )
            }
            Spacer(Modifier.height(18.dp))
            if (workSummaries.isEmpty() && isSyncing ==false){
                EmptyListPlaceHolder("No work data to show", 50 , Icons.Default.WarningAmber)
            }
            if (workSummaries.isEmpty() && isSyncing ==true){

                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }

            }else{
                LaunchedEffect(listState) {
                    snapshotFlow { listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 }
                        .collect { atTop ->
                            if (atTop) {

                                android.util.Log.d("Top scroll", "refresh called ")
                                if (workSummaries.isNotEmpty())
                                        viewModel.refreshData()

                            }
                        }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(workSummaries) {index, daySummary ->

                        if (index == workSummaries.lastIndex && isMoreDataAvailable) {
                            LaunchedEffect(index) {
                                viewModel.loadNextPage(pageSize)
                            }
                        }
                        WorkDaySummaryCard(
                            summary = daySummary,
                            expanded = expandedDays[daySummary.dayLabel] == true,
                            onToggleExpand = { expanded ->
                                expandedDays[daySummary.dayLabel] = expanded
                            },
                            navigator,viewOnlyMode
                        )
                    }
                    if (isMoreDataAvailable) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Show a progress indicator while fetching the next page
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
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
private fun UserDropDown(
    expandedUser: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedUser: AppUser?,
    users: List<AppUser>?,
    viewModel: WorkViewModel
) {


    val searchTerm = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
         TextField(
             modifier = Modifier.fillMaxWidth(),
             value = searchTerm.value,
             onValueChange = {
                 searchTerm.value =it
                 viewModel.filterUsers(it)
             },
             placeholder = { Text("Search...") },
             singleLine = true,
             trailingIcon = {
                 Icon(Icons.Filled.Cancel, contentDescription = "clear text",
                     modifier = Modifier.clickable{
                         searchTerm.value=""
                         viewModel.filterUsers("")
                     })
             }


         )

        ExposedDropdownMenuBox(
            expanded = expandedUser,
            onExpandedChange = { onExpandedChange(!expandedUser) }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedUser?.name ?: "Select worker",
                onValueChange = {},
                label = { Text("Worker") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedUser,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                users?.forEach { user ->
                    DropdownMenuItem(
                        text = {
                            Text("${user.name} ${user.surname} ${user.phoneNumber}")
                        },
                        onClick = {
                            onExpandedChange(false)
                            viewModel.onChangeFilter(
                                WorkDataFiltersDTOState.FilterName,
                                user.id.toString()
                            )
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterModalContent(
    filtersDTO: WorkDataFiltersDTO,
    departments: List<Department>,
    sections: List<Section>,
    viewModel: WorkViewModel,
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern(FILTER_DATE_FORMAT) }
    val openDialog = remember { mutableStateOf(false) }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var selectedMe  = filtersDTO.includeOtherStaffRecords

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
        Text("Filter by:", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.RadioButton(
                    selected = selectedMe,
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
    navigator: NavController,
    viewOnlyMode: Boolean
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
                        StatInline("Volume (Cubes)", covertNumberTo2DecimalString(summary.totalVolume))
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
                        WorkSummaryItem(work, navigator = navigator, viewOnlyMode)
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
        val d = if(value =="null")
                 ""
            else
                value

        Text(d, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
    }
}
/** ✅ WORK SUMMARY ITEM — Enhanced UI **/
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WorkSummaryItem(work: WorkSummaryDTO, navigator: NavController, viewOnlyMode:Boolean) {
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
                        StatInline("📦 Cubes",covertNumberTo2DecimalString(work.totalVolume))
                        StatInline("🗂 Stacks", work.totalStacks.toString())
                        StatInline("👷 Users", work.totalUsers.toString())
                        work.capturedBy?.let {
                        StatInline("✍️ Captured by",
                                if(!viewOnlyMode)
                                    it
                                else
                                extractFullNameFromApiString(it)
                        )
                                }
                        work.capturedAt?.let { StatInline("⏱ Captured at", it) }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        work.updatedBy?.let { StatInline("🔄 Updated by",
                            if(!viewOnlyMode)
                                it
                            else
                            extractFullNameFromApiString(it))
                        }
                        if (work.capturedAt != work.updatedAt && work.updatedAt != "null") {
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
                        showEditButton = (daysPassed <  WORK_DONE_EDIT_GRACE_PERIOD_CAPTURER) && !viewOnlyMode
                    }
                    if(showEditButton){
                        IconButton(
                            onClick = {
                                navigator.navigate(Screen.EditWork.withArgs(work.id.toString()))
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
                work.users.forEach { UserRow(it) }
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
        Text(covertNumberTo3DecimalString(stack.volume), style = MaterialTheme.typography.bodyMedium)
    }
}
@Composable
private fun UserRow(user: UserInfoDTO) {

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
                    text =covertNumberTo3DecimalString(user.personalCubes),
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
@Composable
fun EmptyListPlaceHolder(title:String, imageSize:Int, displayIcon: ImageVector ){

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(displayIcon,
                contentDescription = "",
                modifier = Modifier
                    .size(imageSize.dp)
                    .clip(CircleShape),
            )
            Text(title)
        }
    }
}