package com.example.airdry.domain.workers

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.airdry.domain.use_cases.UserManagement.UploadCachedUsersUseCase
import com.example.airdry.util.operational_states.DownloadState


class UploadUsersWorker(
   appContext: Context,
     params: WorkerParameters,
    private val uploadCachedUsersUseCase: UploadCachedUsersUseCase
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "upload_users_worker"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "upload_users_channel"
    }
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        override suspend fun doWork(): Result {
            createNotificationChannel()
            setForeground(createForegroundInfo("Starting upload..."))
            var result: Result = Result.success()
            uploadCachedUsersUseCase().collect { state ->
                when (state) {
                    is DownloadState.Loading -> {
                        setForeground(createForegroundInfo("Uploading cached users..."))
                    }
                    is DownloadState.Success -> {
                        showCompletedNotification("✅ Upload complete: ${state.message}", true)
                        result = Result.success()
                    }
                    is DownloadState.Error -> {
                        showCompletedNotification("⚠️ Upload failed: ${state.message}", true)
                        result = Result.failure()
                    }
                }
            }
            return result
        }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("Starting upload...")
    }
        private fun createForegroundInfo(progressText: String): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("User Upload")
            .setContentText(progressText)
            .setSmallIcon(R.drawable.stat_sys_upload)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showCompletedNotification(message: String, success: Boolean) {
        // Select sound
        val soundUri = if (success) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("User Upload")
            .setContentText(message)
            .setSmallIcon(R.drawable.stat_sys_upload_done)
            .setOngoing(false)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // multiline support
            .setSound(soundUri) // play sound
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID + 1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Upload Progress"
            val descriptionText = "Shows upload progress"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
