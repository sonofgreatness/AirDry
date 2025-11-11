package com.example.airdry.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.domain.use_cases.StackManagement.DownloadStackDataUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertCasualUsersUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertDepartmentsUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertSectionsUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertUserTypesUseCase
import com.example.airdry.domain.use_cases.UserManagement.UploadCachedUsersUseCase
import com.example.airdry.domain.use_cases.WorkManagement.DownloadAndInsertWorkDataUseCase
import com.example.airdry.domain.use_cases.WorkManagement.WriteWorkUpdatesUseCase
import com.example.airdry.util.Constants.Companion.DEFAULT_OFFSETMONTH_VALUE
import com.example.airdry.util.Constants.Companion.HUMAN_READABLE_FORMAT_TEXT
import com.example.airdry.util.Constants.Companion.OFFSETMONTH_KEY
import com.example.airdry.util.operational_states.DownloadState
import com.example.airdry.util.operational_states.SyncStatus
import com.russhwolf.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val downloadStackDataUseCase: DownloadStackDataUseCase,
    private val downloadAndInsertCasualUsersUseCase: DownloadAndInsertCasualUsersUseCase,
    private val downloadAndInsertDepartmentsUseCase: DownloadAndInsertDepartmentsUseCase,
    private val downloadAndInsertSectionsUseCase: DownloadAndInsertSectionsUseCase,
    private val downloadAndInsertUserTypesUseCase: DownloadAndInsertUserTypesUseCase,
    private val uploadCachedUsersUseCase: UploadCachedUsersUseCase,
    private val downloadAndInsertWorkDataUseCase: DownloadAndInsertWorkDataUseCase,
    private val settings: Settings,
    private val uploadWorkDataUseCase: UploadCachedUsersUseCase,
    private  val writeWorkUpdatesUseCase: WriteWorkUpdatesUseCase
)
    : ViewModel()  {

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()
    private val _offsetMonths = MutableStateFlow(
        settings.getLong(OFFSETMONTH_KEY, DEFAULT_OFFSETMONTH_VALUE)
    )
    val offsetMonths: StateFlow<Long> = _offsetMonths.asStateFlow()
    private val _oldestRecordDateString = MutableStateFlow(
        getOldestRecordDateString(_offsetMonths.value)
    )
    val oldestRecordDateString: StateFlow<String> = _oldestRecordDateString.asStateFlow()

    fun synchronizeData(){
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Working("Starting synchronization...")
            try {
                forcedUpload()
                uploadCachedWork()
                writeCacheUpdateForWork()
                getUserTypes()
                getDepartments()
                getSections()
                downloadStackData()
                getUsers()
                getWorkData()
                _syncStatus.value = SyncStatus.Complete
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Error("Synchronization failed: ${e.localizedMessage}")
            }
        }
    }
    private suspend fun getUserTypes(){
        downloadAndInsertUserTypesUseCase().collect{
            when(it){
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("downloading user types...")
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
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("downloading departments...")
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
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("downloading sections...")
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
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("downloading workers...")
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
                is DownloadState.Loading -> _syncStatus.value = SyncStatus.Working("downloading  stacks...")
                is DownloadState.Success-> _uiEvent.send(it.message)
                is DownloadState.Error ->{
                    _syncStatus.value = SyncStatus.Error("Error: ${it.message}")
                    throw RuntimeException(it.message)
                }
            }
        }
    }
    private suspend  fun forcedUpload(){
        uploadCachedUsersUseCase().collect{
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
    private suspend fun  getWorkData(){
            downloadAndInsertWorkDataUseCase().collect{
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
             }
    }

    fun onUpdateOffsetMonths(newOffset: Long) {
        viewModelScope.launch {
            settings.putLong(OFFSETMONTH_KEY, newOffset)
            _offsetMonths.value = newOffset
            _oldestRecordDateString.value = getOldestRecordDateString(newOffset)
            _uiEvent.send("Data history range updated to $newOffset months.")
        }
    }
    fun getOldestRecordDateString(offsetMonths: Long): String {
        val targetDate = LocalDate.now(ZoneOffset.UTC).minusMonths(offsetMonths)
        val formatter = DateTimeFormatter.ofPattern(HUMAN_READABLE_FORMAT_TEXT)
            .withLocale(Locale.getDefault())
        return targetDate.format(formatter)
    }
    private suspend fun uploadCachedWork(){
            uploadWorkDataUseCase().collect{
                when(it){
                    is DownloadState.Error -> {_uiEvent.send(it.message)}
                    DownloadState.Loading -> {_uiEvent.send("about to upload... ")}
                    is DownloadState.Success -> {_uiEvent.send(it.message)}
                }

            }
    }

    private suspend fun writeCacheUpdateForWork(){
        writeWorkUpdatesUseCase().collect{
            when(it){
                is DownloadState.Error -> {_uiEvent.send(it.message)}
                DownloadState.Loading -> {_uiEvent.send("about to update work data... ")}
                is DownloadState.Success -> {_uiEvent.send(it.message)}
            }

        }
    }
}