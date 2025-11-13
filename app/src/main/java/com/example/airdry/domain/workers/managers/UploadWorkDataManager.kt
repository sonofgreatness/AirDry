package com.example.airdry.domain.workers.managers

import android.app.Application
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.domain.workers.UploadWorkDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

class UploadWorkDataManager @Inject constructor
    (private val connectivityObserver: MyConnectivityObserver,
     private val workDoneRepository: WorkDoneRepository,
     private  val application: Application      ){
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        observeServer()
    }

    @OptIn(FlowPreview::class)
    private fun observeServer() {
        scope.launch {
            connectivityObserver.isServerReachable
                .debounce(1000)
                .collect { reachable ->
                    if (reachable) {
                        val getCachedWork = workDoneRepository.getCountOfWorkDonePendingInsert()
                        Log.d("test3", "cached work  $getCachedWork")
                        if (getCachedWork > 0) {
                            Log.d("test4", "cached users run")
                            enqueueUploadWorker()
                        }
                    }
                }
        }
    }

    private fun enqueueUploadWorker() {
        val workRequest = OneTimeWorkRequestBuilder<UploadWorkDataWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(application)
            .enqueueUniqueWork(
                UploadWorkDataWorker.WORK_NAME,
                ExistingWorkPolicy.KEEP,
                workRequest
            )
    }
}