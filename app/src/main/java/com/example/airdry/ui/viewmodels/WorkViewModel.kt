package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.ConnectivityObserver
import com.example.airdry.auth.SessionManager
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import com.example.airdry.domain.REST_API.interfaces.OrganizationApi
import com.example.airdry.domain.REST_API.interfaces.WorkApi
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import com.example.airdry.util.UtilityFunctions.Companion.toUUIDOrNull
import com.example.airdry.util.operational_states.ExcelReportUiState
import com.example.airdry.util.operational_states.WorkDataFiltersDTOState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val app: Application,
    private val workDoneRepo: WorkDoneRepository,
    private val stackRepo: StackRepository,
    private val userManagementRepo: UserRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val sessionManager: SessionManager,
    private val workApi: WorkApi,
    private val organizationApi: OrganizationApi
) : ViewModel()
{

    private val _selectedAppUser = MutableLiveData<AppUser?>()
    val selectedAppUser: LiveData<AppUser?> = _selectedAppUser

    private val _allStacks = MutableLiveData<List<NestedStack?>>()
    val allStacks : LiveData<List<NestedStack?>>  get() = _allStacks

    private val _uiState = MutableLiveData(ExcelReportUiState())
    val uiState: LiveData<ExcelReportUiState> get() = _uiState

    private val _workDoneWithUsersAndStack = MutableLiveData<List<WorkDoneWithUsersAndStacks>>()
    val workDoneWithUsersAndStack: LiveData<List<WorkDoneWithUsersAndStacks>> = _workDoneWithUsersAndStack

    private val _dailyWorkSummaries = MutableStateFlow<List<WorkDaySummaryDTO>>(emptyList())
    val  dailyWorkSummaries: StateFlow<List<WorkDaySummaryDTO>> = _dailyWorkSummaries

    val isNetworkAvailable: StateFlow<Boolean> = connectivityObserver.isServerReachable
    val isOnlineLoggedIn : StateFlow<Boolean> =  sessionManager.isLoggedInFlow

    private val  _filterDto  = MutableStateFlow<WorkDataFiltersDTO>(WorkDataFiltersDTO())
    val  filterDto: StateFlow<WorkDataFiltersDTO> = _filterDto
    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages
    private val _currentPage = MutableStateFlow(DEFAULT_PAGE)
    val currentPage: StateFlow<Int> = _currentPage
    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val  departments: StateFlow<List<Department>> = _departments
    private val _sections = MutableStateFlow<List<Section>>(emptyList())
    val  sections: StateFlow<List<Section>> = _sections

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()


    init{
        loadAllStacks()
    }

     fun loadAllStacks() {
        viewModelScope.launch{
            stackRepo.getAllNestedStacks().let{
                _allStacks.postValue(it)
            }
        }
    }

    fun clearAllFilters(){
        println("clear filters called")
        _filterDto.value = WorkDataFiltersDTO()
        _totalPages.value = DEFAULT_PAGE
        _currentPage.value = DEFAULT_PAGE // <-- RESET CURRENT PAGE
        populateDailyWorkSummaries()
    }


    fun onChangeFilter(state : WorkDataFiltersDTOState, newValue: String) {
       viewModelScope.launch {
           when(state) {
               is WorkDataFiltersDTOState.IncludeInactiveWorkers -> {
                   _filterDto.value = _filterDto.value.copy(includeInactiveWorkers = state.newValue)
                   populateDailyWorkSummaries()
               }
               is WorkDataFiltersDTOState.FilterDepartment ->{
                   _filterDto.value = _filterDto.value.copy(filterDepartmentId = newValue.toUUIDOrNull())
                   populateSections(UUID.fromString(newValue))
                   populateDailyWorkSummaries()
               }
               is WorkDataFiltersDTOState.FilterDate ->{
                   _filterDto.value = _filterDto.value.copy(filterDate = newValue)
                   populateDailyWorkSummaries()
               }
               is WorkDataFiltersDTOState.FilterName -> {
                   _filterDto.value = _filterDto.value.copy(filterName = newValue)
                   populateDailyWorkSummaries()
               }
               is WorkDataFiltersDTOState.FilterSection ->{
                   _filterDto.value = _filterDto.value.copy(filterSectionId = newValue.toUUIDOrNull())
                   populateDailyWorkSummaries()}
               is WorkDataFiltersDTOState.FilterSurname->{
                   _filterDto.value = _filterDto.value.copy(filterSurname = newValue)
                   populateDailyWorkSummaries()
               }
               is WorkDataFiltersDTOState.FilterPhone ->{
                   _filterDto.value  = _filterDto.value.copy(filterPhoneNumber = newValue)
                   populateDailyWorkSummaries()
               }
               is WorkDataFiltersDTOState.FilterSecondaryPhone -> {
                   _filterDto.value = _filterDto.value.copy(
                       filterSecondaryPhoneNumber = newValue
                   )
                   populateDailyWorkSummaries()
               }
               is WorkDataFiltersDTOState.FilterForMeOnly -> {
                   _filterDto.value = _filterDto.value.copy(includeOtherStaffRecords = state.newValue)
                   populateDailyWorkSummaries()
                   val message = if (state.newValue)
                       "chosen see MY records only "
                   else
                       "chosen see all staff records"
                   _uiEvent.send(message)
               }
           }
       }


    }

    fun loadNextPage(pageSize: Int = DEFAULT_PAGE_SIZE) {
        if (_currentPage.value < _totalPages.value) {
            _currentPage.value++
            populateDailyWorkSummaries(pageSize)
        }
    }
    fun populateDailyWorkSummaries(pageSize: Int = DEFAULT_PAGE_SIZE) {
        val page  = _currentPage.value
        if (page > _totalPages.value) {
            return
        }
        viewModelScope.launch {
            Log.d("nigga give it right back", " network ${isNetworkAvailable.value}    online : ${isOnlineLoggedIn.value}")
            if (isNetworkAvailable.value && isOnlineLoggedIn.value)
            {
                Log.d("nigga give it right back", "1")
            getDataFromServer(page, pageSize)
            }else{
                Log.d("nigga give it right back", "2")
                getDataLocally(page, pageSize)
            }
        }
    }

    private suspend fun getDataFromServer(page: Int, pageSize: Int) {
        println("getDailyWorkSummaries called with date ${filterDto.value.filterDate}")
        val resp = workApi.getDailyWorkSummaries(page, pageSize, filterDto.value)
        resp?.let {
            _totalPages.value = it.totalPages
            if (page <= 1) {
                _dailyWorkSummaries.value = it.items
            } else {
                _dailyWorkSummaries.value += it.items
            }
        }
    }
     private suspend fun getDataLocally(page: Int, pageSize: Int){

      try {

          val  test  = workDoneRepo.getAllWorkDoneWithUsersAndStack()
          Log.d("nigga give it right back", "1.9 filter ${test}")

          Log.d("nigga give it right back", "2 filter ${filterDto.value}")
          val resp = workDoneRepo.getDailyWorkSummaries(page, pageSize, filterDto.value)
          Log.d("nigga give it right back", "2 res ${resp?.items?.size} page : $page . pageSize: $pageSize")
          resp?.let {
              _totalPages.value = it.totalPages
              if (page <= 1) {
                  _dailyWorkSummaries.value = it.items
              } else {
                  _dailyWorkSummaries.value += it.items
              }
          }

      }catch (e: Exception){
          Log.d("nigga give it back 3.1", "failed ${e.message}")
      }

     }

    fun populateDepartmentsAndSections(){
        populateDepartments()
    }
    private fun populateDepartments(){
        viewModelScope.launch {
            try {
                Log.d("workViewModel", "Populating departments")
                pickDepartmentDataSource(isNetworkAvailable.value,isOnlineLoggedIn.value)
            }catch (e: Exception) {
                Log.e("workViewModel", "failed to populate departments Error ${e.message}")
            }
        }
    }

    private suspend fun pickDepartmentDataSource(isNetworkAvailable: Boolean = false,isOnlineLoggedIn: Boolean) {
        try{
            if (isNetworkAvailable &&isOnlineLoggedIn){
                val departmentsVariable : List<Department> =organizationApi.getAllDept().map {
                    it.toDepartment()
                }
                _departments.value = departmentsVariable
            }else{
                val  departmentsVariable : List<Department> = userManagementRepo.getAllDepartments()?:emptyList()
                _departments.value = departmentsVariable
            }
        }catch (e: Exception){
            Log.e("workViewModel", "failed to populate departments pickDepartmentDataSourceError ${e.message}")
        }
    }

    private  fun  populateSections(departmentId:UUID){
        viewModelScope.launch {
            try{
                pickSectionDataSource(isNetworkAvailable.value,departmentId, isOnlineLoggedIn.value)
            }
            catch (e: Exception){
                Log.e("workViewModel", "failed to populate sections Error ${e.message}")
            }
        }
    }
    private  suspend fun  pickSectionDataSource(isNetworkAvailable: Boolean = false,
                                                departmentId:UUID, isOnlineLoggedIn: Boolean){
        try {
            if(isNetworkAvailable && isOnlineLoggedIn){
                val sections = organizationApi.getSectionByDepartmentId(departmentId)?.map{ it.toSection()}
                _sections.value = sections?:emptyList()
            }
            else{
                val sections = userManagementRepo.getDepartmentSections(departmentId)
                _sections.value = sections?:emptyList()
            }
        }  catch (e: Exception){
            Log.d("populate sections", "exception ${e.message}")
        }
    }
    suspend  fun getEmployeeById(employeeId: UUID): NestedAppUser? {
        return userManagementRepo.getById(employeeId)
    }
}
