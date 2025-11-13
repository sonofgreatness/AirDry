package com.example.airdry

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.airdry.domain.workers.factories.MyNewFactory
import com.example.airdry.domain.workers.factories.MyWorkerFactory
import com.example.airdry.domain.workers.managers.UpdateWorkDataManager
import com.example.airdry.domain.workers.managers.UploadUsersManager
import com.example.airdry.domain.workers.managers.UploadWorkDataManager
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL_DESCRIPTION
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL_DESCRIPTION_MAIN
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_CHANNEL
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_CHANNEL_DESCRIPTION
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AirDryApplication: Application(), Configuration.Provider {

@Inject lateinit var workerFactory: MyNewFactory
@Inject lateinit var updateWorkDataManager: UpdateWorkDataManager// do not remove
@Inject lateinit var  uploadUsersManager: UploadUsersManager
@Inject lateinit var uploadWorkDataManager: UploadWorkDataManager
override fun getWorkManagerConfiguration() =
    Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

}