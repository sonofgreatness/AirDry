package com.example.airdry.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.domain.use_cases.WorkManagement.DownloadAndInsertWorkDataUseCase
import com.example.airdry.util.operational_states.DownloadState
import com.example.airdry.util.operational_states.ExcelReportUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val app: Application,
    private val workDoneRepo: WorkDoneRepository,
    private val stackRepo: StackRepository,
    private val employeeRepo: UserRepository) : ViewModel()
{

    private val _selectedAppUser = MutableLiveData<AppUser?>()
    val selectedAppUser: LiveData<AppUser?> = _selectedAppUser

    private val _allStacks = MutableLiveData<List<NestedStack?>>()
    val allStacks : LiveData<List<NestedStack?>>  get() = _allStacks

    private val _uiState = MutableLiveData(ExcelReportUiState())
    val uiState: LiveData<ExcelReportUiState> get() = _uiState

    private val _workDoneWithUsersAndStack = MutableLiveData<List<WorkDoneWithUsersAndStacks>>()
    val workDoneWithUsersAndStack: LiveData<List<WorkDoneWithUsersAndStacks>> = _workDoneWithUsersAndStack
    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()


    init{
       loadWorkDoneWithUsersAndStacks()
        loadAllStacks()
    }

     fun loadAllStacks() {
        viewModelScope.launch{
            stackRepo.getAllNestedStacks().let{
                _allStacks.postValue(it)
            }
        }
    }

    fun selectEmployee(appUser: AppUser) {
        _selectedAppUser.value = appUser
    }
    suspend fun getEmployeeById(id: UUID): NestedAppUser? {
        return employeeRepo.getById(id)
    }

    fun loadWorkDoneWithUsersAndStacks(){
      viewModelScope.launch {
        /* downloadAndInsertWorkDataUseCase().collect{
             when(it){
                 is DownloadState.Error -> {
                     _uiEvent.send(it.message)
                 }
                 DownloadState.Loading -> {
                     _uiEvent.send("downloading work data ...")
                 }
                 is DownloadState.Success -> {
                     _uiEvent.send(it.message)
                 }
             }
         }*/
       }
    }
}
