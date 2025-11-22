package com.example.airdry

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.airdry.domain.workers.SyncAppDataWorker
import com.example.airdry.domain.workers.factories.MyNewFactory
import com.example.airdry.domain.workers.factories.MyWorkerFactory
import com.example.airdry.domain.workers.managers.UpdateWorkDataManager
import com.example.airdry.domain.workers.managers.UploadUsersManager
import com.example.airdry.domain.workers.managers.UploadWorkDataManager
import com.example.airdry.util.Constants.Companion.DEFAULT_OFFSETDAY_VALUE
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL_DESCRIPTION
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL_DESCRIPTION_MAIN
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_CHANNEL
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_CHANNEL_DESCRIPTION
import com.example.airdry.util.Constants.Companion.OFFSETDAY_KEY
import com.russhwolf.settings.Settings
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class AirDryApplication: Application(), Configuration.Provider {

@Inject lateinit var workerFactory: MyNewFactory
@Inject lateinit var updateWorkDataManager: UpdateWorkDataManager// do not remove
@Inject lateinit var  uploadUsersManager: UploadUsersManager
@Inject lateinit var uploadWorkDataManager: UploadWorkDataManager
@Inject lateinit var settings: Settings
override fun getWorkManagerConfiguration() =
    Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()




    override fun onCreate() {
        super.onCreate()
        // Schedule periodic sync on app startup
        val updateDays = settings.getLong(OFFSETDAY_KEY, DEFAULT_OFFSETDAY_VALUE)
        schedulePeriodicSync(updateDays)
    }

    private fun schedulePeriodicSync(updateDays: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<SyncAppDataWorker>(
            updateDays, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15, TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncAppDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

}