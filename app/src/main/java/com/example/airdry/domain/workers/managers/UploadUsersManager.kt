package com.example.airdry.domain.workers.managers

import android.app.Application
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.workers.UploadUsersWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

class UploadUsersManager @Inject constructor
    (private val connectivityObserver: MyConnectivityObserver,
     private val userRepository: UserRepository,
     private  val application: Application      ){
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        Log.d("test0", "init called")
        observeServer()
    }

    @OptIn(FlowPreview::class)
    private fun observeServer() {
        scope.launch {
            connectivityObserver.isServerReachable
                .debounce(1000)
                .collect { reachable ->
                    if (reachable) {
                        val getCachedUsers = userRepository.getCountOfUsersPendingInsert()
                        Log.d("test", "cached users $getCachedUsers")
                        if (getCachedUsers > 0) {
                            Log.d("test2", "cached users run")
                        enqueueUploadWorker()
                        }
                    }
                }
        }
    }

    private fun enqueueUploadWorker() {
        val workRequest = OneTimeWorkRequestBuilder<UploadUsersWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(application)
            .enqueueUniqueWork(
                UploadUsersWorker.WORK_NAME,
                ExistingWorkPolicy.KEEP,
                workRequest
            )
    }
}