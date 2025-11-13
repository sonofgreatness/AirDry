package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.domain.use_cases.StackManagement.DownloadStackDataUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertCasualUsersUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertDepartmentsUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertSectionsUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertUserTypesUseCase
import com.example.airdry.domain.use_cases.UserManagement.UploadCachedUsersUseCase
import com.example.airdry.domain.use_cases.WorkManagement.DownloadAndInsertWorkDataUseCase
import com.example.airdry.domain.use_cases.WorkManagement.WriteWorkUpdatesUseCase
import com.example.airdry.domain.workers.SyncAppDataWorker
import com.example.airdry.util.Constants.Companion.DEFAULT_OFFSETDAY_VALUE
import com.example.airdry.util.Constants.Companion.DEFAULT_OFFSETMONTH_VALUE
import com.example.airdry.util.Constants.Companion.HUMAN_READABLE_FORMAT_TEXT
import com.example.airdry.util.Constants.Companion.OFFSETDAY_KEY
import com.example.airdry.util.Constants.Companion.OFFSETMONTH_KEY
import com.example.airdry.util.operational_states.DownloadState
import com.example.airdry.util.operational_states.SyncStatus
import com.russhwolf.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: Settings,
    private val application : Application,
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

    private val _offsetDays =MutableStateFlow(
        settings.getLong(OFFSETDAY_KEY, DEFAULT_OFFSETDAY_VALUE)
    )
    val offsetDays: StateFlow<Long> = _offsetDays.asStateFlow()

    private val _oldestRecordDateString = MutableStateFlow(
        getOldestRecordDateString(_offsetMonths.value)
    )
    val oldestRecordDateString: StateFlow<String> = _oldestRecordDateString.asStateFlow()

    fun synchronizeData(){
        Log.d("sync", "sync called")
        val workManager = WorkManager.getInstance(application)
        val workRequest = OneTimeWorkRequestBuilder<SyncAppDataWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueueUniqueWork(
            SyncAppDataWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        viewModelScope.launch {
            workManager.getWorkInfoByIdLiveData(workRequest.id)
                .asFlow()
                .filterNotNull()
                .collect { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> {
                            _syncStatus.value = SyncStatus.Working("⏳ Preparing synchronization...")
                        }

                        WorkInfo.State.RUNNING -> {
                            val progress = workInfo.progress.getInt("Progress", 0)
                            val message = workInfo.progress.getString("Message") ?: "Synchronizing..."
                            _syncStatus.value = SyncStatus.Working("🔄 $message ($progress%)")
                        }

                        WorkInfo.State.SUCCEEDED -> {
                            _syncStatus.value = SyncStatus.Complete
                            _uiEvent.send("✅ Synchronization completed successfully")
                        }

                        WorkInfo.State.FAILED -> {
                            _syncStatus.value = SyncStatus.Error("❌ Synchronization failed")
                        }

                        WorkInfo.State.CANCELLED -> {
                            _syncStatus.value = SyncStatus.Error("❌ Synchronization cancelled")
                        }

                        else -> Unit
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
    fun onUpdateOffsetDays(newOffset: Long) {
        viewModelScope.launch {
            settings.putLong(OFFSETDAY_KEY, newOffset)
            _offsetDays.value = newOffset
            _uiEvent.send("Update data every   $newOffset days.")

        }
    }
    fun getOldestRecordDateString(offsetMonths: Long): String {
        val targetDate = LocalDate.now(ZoneOffset.UTC).minusMonths(offsetMonths)
        val formatter = DateTimeFormatter.ofPattern(HUMAN_READABLE_FORMAT_TEXT)
            .withLocale(Locale.getDefault())
        return targetDate.format(formatter)
    }

}