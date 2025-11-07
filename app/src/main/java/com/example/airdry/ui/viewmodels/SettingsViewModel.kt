package com.example.airdry.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.domain.use_cases.StackManagement.DownloadStackDataUseCase
import com.example.airdry.domain.use_cases.UploadCachedUsers
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertCasualUsersUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertDepartmentsUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertSectionsUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertUserTypesUseCase
import com.example.airdry.util.operational_states.DownloadState
import com.example.airdry.util.operational_states.SyncStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val downloadStackDataUseCase: DownloadStackDataUseCase,
    private val  downloadAndInsertCasualUsersUseCase: DownloadAndInsertCasualUsersUseCase,
    private  val downloadAndInsertDepartmentsUseCase: DownloadAndInsertDepartmentsUseCase,
    private  val downloadAndInsertSectionsUseCase: DownloadAndInsertSectionsUseCase,
    private  val downloadAndInsertUserTypesUseCase: DownloadAndInsertUserTypesUseCase,
    private val uploadCachedUsers: UploadCachedUsers
): ViewModel()  {

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    fun synchronizeData(){
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Working("Starting synchronization...")
            try {
                forcedUpload()
                getUserTypes()
                getDepartments()
                getSections()
                getUsers()
                downloadStackData()
                _syncStatus.value = SyncStatus.Complete
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error("Synchronization failed: ${e.localizedMessage}")
            }
        }
    }
    private suspend fun getUserTypes(){
        downloadAndInsertUserTypesUseCase().collect{
            when(it){
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("Downloading user types...")
                is DownloadState.Success-> _uiEvent.send(it.message) // Keep toast for final success
                is DownloadState.Error -> {
                    _syncStatus.value = SyncStatus.Error("Error: ${it.message}")
                    throw RuntimeException(it.message) // Stop process on error
                }
            }
        }
    }
    private suspend fun  getDepartments(){
        downloadAndInsertDepartmentsUseCase().collect{
            when(it){
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("Downloading departments...")
                is DownloadState.Success-> _uiEvent.send(it.message)
                is DownloadState.Error ->{
                    _syncStatus.value = SyncStatus.Error("Error: ${it.message}")
                    throw RuntimeException(it.message)
                }
            }
        }
    }

    private suspend fun  getSections(){
        downloadAndInsertSectionsUseCase().collect{
            when(it){
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("Downloading sections...")
                is DownloadState.Success-> _uiEvent.send(it.message)
                is DownloadState.Error ->{
                    _syncStatus.value = SyncStatus.Error("Error: ${it.message}")
                    throw RuntimeException(it.message)
                }
            }
        }
    }
    private suspend fun getUsers(){
        downloadAndInsertCasualUsersUseCase().collect{
            when(it){
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("Downloading workers...")
                is DownloadState.Success-> _uiEvent.send(it.message)
                is DownloadState.Error ->{
                    _syncStatus.value = SyncStatus.Error("Error: ${it.message}")
                    throw RuntimeException(it.message)
                }
            }
        }
    }
    private suspend fun downloadStackData() {
        downloadStackDataUseCase().collect{
            when(it){
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("Downloading  stacks...")
                is DownloadState.Success-> _uiEvent.send(it.message)
                is DownloadState.Error ->{
                    _syncStatus.value = SyncStatus.Error("Error: ${it.message}")
                    throw RuntimeException(it.message)
                }
            }
        }
    }
    private suspend  fun forcedUpload(){
        uploadCachedUsers().collect{
            when(it){
                is DownloadState.Error -> {
                    _syncStatus.value = SyncStatus.Error("Error: ${it.message}")
                    throw RuntimeException(it.message)
                }
                DownloadState.Loading -> {
                    _syncStatus.value = SyncStatus.Working("Uploading cached users...")
                }
                is DownloadState.Success -> {
                    _uiEvent.send(it.message)
                }
            }
        }
    }
}