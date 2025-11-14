package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Organization
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.database.Entities.UserManagement.UserType
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.workers.UploadUsersWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel
    @Inject constructor(private val application: Application,
                        private val repository: UserRepository,
        connectivityObserver: MyConnectivityObserver):
    ViewModel() {

    val isServerReachable = connectivityObserver.isServerReachable
    private val _filteredEmployees = MutableLiveData<List<NestedAppUser>>()

    val filteredEmployees :LiveData<List<NestedAppUser>>
        get() = _filteredEmployees

    private val  _useTypes  = MutableStateFlow<List<UserType>> (emptyList())
    val useTypes: StateFlow<List<UserType>> = _useTypes

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val  departments: StateFlow<List<Department>> = _departments

    private val _sections = MutableStateFlow<List<Section>>(emptyList())
    val  sections: StateFlow<List<Section>> = _sections

    private val _organization = MutableStateFlow<Organization?>(null)
    val  organization : StateFlow<Organization?> = _organization

    private val _isSyncing = MutableStateFlow(true)
    val isSyncing : StateFlow<Boolean> get() =_isSyncing

    init {
        loadAllEmployees()
        getOrganization()
        populateDropDowns()
    }
    fun loadAllEmployees() {
        viewModelScope.launch {
            val all = repository.getFilteredEmployees("") ?: emptyList()
            _filteredEmployees.postValue(all)
        }
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
     fun createEmployee(entity: AppUser) {
        viewModelScope.launch {
            Toast.makeText(application, "Employee Created", Toast.LENGTH_SHORT).show()
            repository.createEmployee(entity)
        }
    }

    fun updateFilteredEmployees(searchTerm :String){
    viewModelScope.launch{
        _isSyncing.value = true
        repository.getFilteredEmployees(searchTerm).let{
            _filteredEmployees.postValue(it)
        }
        delay(2000)
        _isSyncing.value = false
    }
    }


    suspend fun getEmployeeById(id: UUID): NestedAppUser? {
        return repository.getById(id)
    }
     fun  deleteEmployee(worker: AppUser){
     viewModelScope.launch {
         repository.deleteEmployee(worker)
     }
    }
    fun forcedUpload(){
        val workRequest = OneTimeWorkRequestBuilder<UploadUsersWorker>().build()
        val workManager = WorkManager.getInstance(application)

        workManager.enqueueUniqueWork(
            UploadUsersWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        viewModelScope.launch {
            workManager.getWorkInfoByIdLiveData(workRequest.id)
                .asFlow()
                .filterNotNull()
                .map { it.state }
                .distinctUntilChanged()
                .collect { state ->
                    when (state) {
                        WorkInfo.State.SUCCEEDED -> loadAllEmployees()
                        WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                            Log.d("forcedUpload", "Upload failed or cancelled")
                        }
                        else -> { /* do nothing */ }
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