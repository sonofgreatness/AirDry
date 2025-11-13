package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.auth.SessionManager
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.domain.REST_API.interfaces.OrganizationApi
import com.example.airdry.domain.REST_API.interfaces.WorkApi
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import com.example.airdry.util.UtilityFunctions.Companion.toUUIDOrNull
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
    private val myConnectivityObserver: MyConnectivityObserver,
    private val sessionManager: SessionManager,
    private val workApi: WorkApi,
    private val organizationApi: OrganizationApi
) : ViewModel()
{

    private val _selectedAppUser = MutableLiveData<AppUser?>()
    val selectedAppUser: LiveData<AppUser?> = _selectedAppUser

    private val _allStacks = MutableLiveData<List<NestedStack?>>()
    val allStacks : LiveData<List<NestedStack?>>  get() = _allStacks


    private val _dailyWorkSummaries = MutableStateFlow<List<WorkDaySummaryDTO>>(emptyList())
    val  dailyWorkSummaries: StateFlow<List<WorkDaySummaryDTO>> = _dailyWorkSummaries

    val isNetworkAvailable: StateFlow<Boolean> = myConnectivityObserver.isServerReachable
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

    private val _viewModeOnly = MutableStateFlow<Boolean>(false)
    val viewModeOnly: StateFlow<Boolean> get() = _viewModeOnly


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
    fun setViewMode(value:Boolean){
        _viewModeOnly.value  = value
    }

    fun clearAllFilters(){
        _filterDto.value = WorkDataFiltersDTO()
        _totalPages.value = DEFAULT_PAGE
        _currentPage.value = DEFAULT_PAGE // <-- RESET CURRENT PAGE
        populateDailyWorkSummaries()
    }
    fun refreshData(){
        _totalPages.value = DEFAULT_PAGE
        _currentPage.value = DEFAULT_PAGE // <-- RESET CURRENT PAGE
        _dailyWorkSummaries.value = emptyList()
        populateDailyWorkSummaries()
    }


    fun onChangeFilter(state : WorkDataFiltersDTOState, newValue: String) {
       viewModelScope.launch {
           _currentPage.value = DEFAULT_PAGE
           _totalPages.value =DEFAULT_PAGE
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
                       "chosen see all staff records"
                   else
                       "chosen see MY records only "
                   _uiEvent.send(message)
               }
           }
       }


    }

    fun loadNextPage(pageSize: Int = DEFAULT_PAGE_SIZE, viewModeOnly: Boolean =_viewModeOnly.value) {
        if (_currentPage.value < _totalPages.value) {
            _currentPage.value++
            populateDailyWorkSummaries(pageSize, viewModeOnly)
        }
    }
    fun populateDailyWorkSummaries(pageSize: Int = DEFAULT_PAGE_SIZE, viewModeOnly:Boolean =_viewModeOnly.value) {
        val page  = _currentPage.value
        if (page > _totalPages.value) {
            return
        }
        viewModelScope.launch {
            if (isNetworkAvailable.value && isOnlineLoggedIn.value && viewModeOnly)
            {
                Log.d("load data", "server")
            getDataFromServer(page, pageSize)
            }else{
                Log.d("load data", "local")
                getDataLocally(page, pageSize)
            }
        }
    }

    private suspend fun getDataFromServer(page: Int, pageSize: Int) {
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
          val resp = workDoneRepo.getDailyWorkSummaries(page, pageSize, filterDto.value)
          resp?.let {
              _totalPages.value = it.totalPages
              if (page <= 1) {
                  _dailyWorkSummaries.value = it.items
              } else {
                  _dailyWorkSummaries.value += it.items
              }
          }

      }catch (e: Exception){
          Log.d("getDataLocally", "failed ${e.message}")
      }
     }

    fun populateDepartmentsAndSections(){
        populateDepartments()
    }
    private fun populateDepartments(){
        viewModelScope.launch {
            try {
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

}
