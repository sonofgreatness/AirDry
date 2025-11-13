package com.example.airdry.domain.workers

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.domain.use_cases.StackManagement.DownloadStackDataUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertCasualUsersUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertDepartmentsUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertSectionsUseCase
import com.example.airdry.domain.use_cases.UserManagement.DownloadAndInsertUserTypesUseCase
import com.example.airdry.domain.use_cases.UserManagement.UploadCachedUsersUseCase
import com.example.airdry.domain.use_cases.WorkManagement.DownloadAndInsertWorkDataUseCase
import com.example.airdry.domain.use_cases.WorkManagement.UploadWorkDataUseCase
import com.example.airdry.domain.use_cases.WorkManagement.WriteWorkUpdatesUseCase
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow


class SyncAppDataWorker (
     appContext: Context,
     params: WorkerParameters,
    private val downloadStackDataUseCase: DownloadStackDataUseCase,
    private  val downloadAndInsertWorkDataUseCase: DownloadAndInsertWorkDataUseCase,
    private val downloadAndInsertCasualUsersUseCase: DownloadAndInsertCasualUsersUseCase,
    private val downloadAndInsertDepartmentsUseCase: DownloadAndInsertDepartmentsUseCase,
    private val downloadAndInsertSectionsUseCase: DownloadAndInsertSectionsUseCase,
    private val downloadAndInsertUserTypesUseCase: DownloadAndInsertUserTypesUseCase,
    private val uploadCachedUsersUseCase: UploadCachedUsersUseCase,
    private val uploadWorkDataUseCase: UploadWorkDataUseCase,
    private val writeWorkUpdatesUseCase: WriteWorkUpdatesUseCase,
    private  val connectivityObserver: MyConnectivityObserver
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "sync_app_data"
        const val CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_ID = 1010
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val isServerReachable = connectivityObserver.isServerReachable.value
        createNotificationChannel()
        setForeground(createForegroundInfo("Starting synchronization...", 0))

        val tasks = listOf(
            "Uploading cached users" to { uploadCachedUsersUseCase() },
            "Uploading cached work" to { uploadWorkDataUseCase() },
            "Writing work updates" to { writeWorkUpdatesUseCase() },
            "Downloading user types" to { downloadAndInsertUserTypesUseCase() },
            "Downloading departments" to { downloadAndInsertDepartmentsUseCase() },
            "Downloading sections" to { downloadAndInsertSectionsUseCase() },
            "Downloading stacks" to { downloadStackDataUseCase() },
            "Downloading workers" to { downloadAndInsertCasualUsersUseCase() },
            "Downloading work data" to { downloadAndInsertWorkDataUseCase() }
        )

        val totalTasks = tasks.size
        if (isServerReachable == false) {
            Log.d("sync data worker", "server  NOT reached")
            return Result.retry()
        }
         else{


            try {
                Log.d("sync data worker", "server reached")
                for ((index, task) in tasks.withIndex()) {
                    val (label, useCase) = task
                    val progressPercent = ((index.toFloat() / totalTasks) * 100).toInt()
                    val progressText = "Step ${index + 1} of $totalTasks: $label ($progressPercent%)"

                    updateProgress(progressText, progressPercent)
                    val result = runUseCase(label, useCase)

                    if (!result) {
                        showCompletedNotification("❌ Failed during: $label", success = false)
                        return Result.failure()
                    }
                }

                showCompletedNotification("✅ Synchronization completed successfully", success = true)
                return Result.success()
            } catch (e: Exception) {
                showCompletedNotification("❌ Sync failed: ${e.localizedMessage}", success = false)
                return Result.failure()
            }
         }



    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("Preparing synchronization...", 0)
    }
    private suspend fun runUseCase(
        label: String,
        useCase: suspend () -> Flow<DownloadState>
    ): Boolean {
        var success = true
        useCase().collect { state ->
            when (state) {
                is DownloadState.Loading -> updateProgress("🔄 $label...", -1)
                is DownloadState.Success -> updateProgress("✅ ${state.message}", -1)
                is DownloadState.Error -> {
                    updateProgress("❌ ${state.message}", -1)
                    success = false
                }
            }
        }
        return success
    }
    private fun createForegroundInfo(progressText: String, percent: Int): ForegroundInfo {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("App Synchronization")
            .setContentText(progressText)
            .setSmallIcon(R.drawable.stat_notify_sync)
            .setStyle(NotificationCompat.BigTextStyle().bigText(progressText))
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (percent in 0..100) {
            builder.setProgress(100, percent, false)
        }

        return ForegroundInfo(NOTIFICATION_ID, builder.build())
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showCompletedNotification(message: String, success: Boolean) {
        val soundUri = if (success)
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        else
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("App Synchronization")
            .setContentText(message)
            .setSmallIcon(if (success) R.drawable.stat_notify_sync else R.drawable.stat_notify_error)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSound(soundUri)
            .setOngoing(false)
            .build()
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID + 1, notification)
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Synchronization",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of background synchronization"
            }
            val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private suspend fun updateProgress(message: String, percent: Int) {
        // Update foreground notification
        setForeground(createForegroundInfo(message, percent))

        // Report to WorkManager for UI updates
        setProgress(
            workDataOf(
                "Message" to message,
                "Progress" to percent.coerceIn(0, 100)
            )
        )
    }
}