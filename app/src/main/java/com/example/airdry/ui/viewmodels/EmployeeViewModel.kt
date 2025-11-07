package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.data.dto.Organization.CreateOrgDTO
import com.example.airdry.data.dto.Organization.CreateSectDTO
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Organization
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.database.Entities.UserManagement.UserType
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInserCasualUsers
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertDepartments
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertSections
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertUserTypes
import com.example.airdry.domain.use_cases.UploadCachedUsers
import com.example.airdry.util.operational_states.DownloadState
import com.example.airdry.util.operational_states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.annotation.meta.When
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel
    @Inject constructor(private val application: Application,
                       private val repository: UserRepository,
        private val  downloadAndInserCasualUsers: DownloadAndInserCasualUsers,
        private  val downloadAndInsertDepartments: DownloadAndInsertDepartments,
        private  val downloadAndInsertSections: DownloadAndInsertSections,
        private  val downloadAndInsertUserTypes: DownloadAndInsertUserTypes,
        private val uploadCachedUsers: UploadCachedUsers):
    ViewModel() {
    private val _allEmployees = MutableLiveData<List<NestedAppUser>>()
    val allEmployees: LiveData<List<NestedAppUser>>
        get() = _allEmployees
    private val _filteredEmployees = MutableLiveData<List<AppUser>>()
    val filteredEmployees :LiveData<List<AppUser>>
        get() = _filteredEmployees
    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val  _useTypes  = MutableStateFlow<List<UserType>> (emptyList())
    val useTypes: StateFlow<List<UserType>> = _useTypes

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val  departments: StateFlow<List<Department>> = _departments

    private val _sections = MutableStateFlow<List<Section>>(emptyList())
    val  sections: StateFlow<List<Section>> = _sections

    private val _organization = MutableStateFlow<Organization?>(null)
    val  organization : StateFlow<Organization?> = _organization
    init {
        loadAllEmployees()
        getAllEmployees()
        getOrganization()
        populateDropDowns()

    }
    fun populateDropDowns(){
        populateSections()
        populateUserTypes()
        populateDepartments()
    }
    private fun populateDepartments() {
        viewModelScope.launch {
            repository.getAllDepartments()?.let {
              _departments.value = it
            }
        }
    }
    private fun populateSections() {
      viewModelScope.launch {
          repository.getAllSections()?.let{
              _sections.value=it
          }
      }
    }

    private fun populateUserTypes() {
    viewModelScope.launch {
        repository.getCasualUserType()?.let{
            _useTypes.value=it
        }
    }
    }

    private fun getOrganization() {
       viewModelScope.launch {
           repository.getOganization()?.let{
               _organization.value = it
           }
       }
    }
    fun superWork(){
        viewModelScope.launch {
          /*getUserTypes()
            delay(100)
            getDepartments()
            delay(100)
            getSections()
            delay(100)
            getUsers()*/
        }
    }

    private suspend fun getUserTypes(){
        downloadAndInsertUserTypes().collect{
            when(it){

               is DownloadState.Loading -> {
                    _uiEvent.send("downloading user types ...")
                }
                 is DownloadState.Success-> {
                    _uiEvent.send(it.message)
                }
                is DownloadState.Error ->{
                    _uiEvent.send(it.message)
                }
            }
        }
    }
    private suspend fun  getDepartments(){
        downloadAndInsertDepartments().collect{
            when(it){

                is DownloadState.Loading -> {
                    _uiEvent.send("downloading departments ...")
                }
                is DownloadState.Success-> {
                    _uiEvent.send(it.message)
                }
                is DownloadState.Error ->{
                    _uiEvent.send(it.message)
                }
            }
        }
    }

    private suspend fun  getSections(){
        downloadAndInsertSections().collect{
            when(it){

                is DownloadState.Loading -> {
                    _uiEvent.send("downloading sections ...")
                }
                is DownloadState.Success-> {
                    _uiEvent.send(it.message)
                }
                is DownloadState.Error ->{
                    _uiEvent.send(it.message)
                }
            }
        }
    }
    private suspend fun getUsers(){
        downloadAndInserCasualUsers().collect{
                when(it){
                    is DownloadState.Loading -> {
                        _uiEvent.send("downloading users ...")
                    }
                    is DownloadState.Success-> {
                        _uiEvent.send(it.message)
                    }
                    is DownloadState.Error ->{
                        _uiEvent.send(it.message)
                    }
                }

        }
    }
     fun getAllEmployees() {
        viewModelScope.launch {
          _allEmployees.value = repository.getAllNestedEmployees().toList()
        }
    }
     fun createEmployee(entity: AppUser) {
        viewModelScope.launch {
            Toast.makeText(application, "Employee Created", Toast.LENGTH_SHORT).show()
            repository.createEmployee(entity)
        }
    }
     fun editEmployee(entity: AppUser) {
        viewModelScope.launch {
            Toast.makeText(application, "Employee Edited", Toast.LENGTH_SHORT).show()
            repository.editEmployee(entity)
            getAllEmployees()
        }
    }
    fun updateFilteredEmployees(searchTerm :String){
    viewModelScope.launch{
        repository.getFilteredEmployees(searchTerm).let{
            _filteredEmployees.postValue(it)
        }
    }
    }

    fun loadAllEmployees() {
        viewModelScope.launch {
            val all = repository.getFilteredEmployees("") ?: emptyList()
            _filteredEmployees.postValue(all)
            Log.d("loadEmployees", " loadEmployees called size -> ${all.size}")
        }
    }
    suspend fun getEmployeeById(id: UUID): AppUser? {
        return repository.getById(id)
    }
     fun  deleteEmployee(worker: AppUser){
     viewModelScope.launch {
         repository.deleteEmployee(worker)
     }
    }
    fun forcedUpload(){
        viewModelScope.launch {
            Log.d("forcedUpload", "forcedUpload called")
            uploadCachedUsers().collect{
                when(it){
                    is DownloadState.Error -> {
                        _uiEvent.send(it.message)
                    }
                    DownloadState.Loading -> {
                        _uiEvent.send("Updating users...")
                    }
                    is DownloadState.Success -> {
                        Log.d("forcedUpload", "forcedUpload success")
                        _uiEvent.send(it.message)
                    }
                }
            }
        }
    }
   fun reloadSectionsByDepartment(departmentId :UUID){
       viewModelScope.launch {
           repository.getDepartmentSections(departmentId)?.let{
               _sections.value =it
           }
       }
   }
}