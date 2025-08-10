package com.example.airdry

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_CHANNEL
import dagger.hilt.android.HiltAndroidApp

import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.domain.workers.factories.MyWorkerFactory
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL_DESCRIPTION
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL_DESCRIPTION_MAIN
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_CHANNEL_DESCRIPTION
import javax.inject.Inject

@HiltAndroidApp
class AirDryApplication: Application(), Configuration.Provider {


    @Inject lateinit var myWorkerFactory: MyWorkerFactory


    override fun onCreate() {
        super.onCreate()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("met1", "channel created")
            val channel = NotificationChannel(
                GENERATE_FILE_CHANNEL,
                GENERATE_FILE_CHANNEL_DESCRIPTION,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Used for foreground notifications while generating Excel reports."
            }


            val channel2 = NotificationChannel(
                DOWNLOAD_FILE_CHANNEL,
                DOWNLOAD_FILE_CHANNEL_DESCRIPTION,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = DOWNLOAD_FILE_CHANNEL_DESCRIPTION_MAIN
            }

            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(channel2)
        }

    }



    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(myWorkerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }

}