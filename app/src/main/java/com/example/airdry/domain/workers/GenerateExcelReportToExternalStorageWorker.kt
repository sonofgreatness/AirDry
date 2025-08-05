package com.example.airdry.domain.workers

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.airdry.R
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_CHANNEL
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_NOTIFICATION_DESCRIPTION
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_NOTIFICATION_TITLE
import com.example.airdry.util.Constants.Companion.NOTIFICATION_ID
import com.example.airdry.util.FileGenerationUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


class GenerateExcelReportToExternalStorageWorker @AssistedInject constructor(
    private val employeeRepo: EmployeeRepository,
    private val workRepo: WorkDoneRepository,
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(appContext, params)

{

    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): GenerateExcelReportToExternalStorageWorker
    }

    override suspend fun doWork(): Result {
        startForegroundService()
        val generator =  FileGenerationUtil(appContext
            ,employeeRepo, workRepo
        )


        val startDate = params.inputData.getString("startDate").toString()
        val endDate = params.inputData.getString("endDate").toString()
        generator.writeExternalWorkBook(startDate, endDate)
        return Result.success()

    }






    private suspend fun startForegroundService() {
        Log.d("met", "startForegroundService called ")
        setForeground(
            ForegroundInfo(
                NOTIFICATION_ID,
                NotificationCompat.Builder(appContext,
                    GENERATE_FILE_CHANNEL)
                    .setSmallIcon(R.drawable.baseline_file_present_24)
                    .setContentText(GENERATE_FILE_NOTIFICATION_DESCRIPTION)
                    .setContentTitle(GENERATE_FILE_NOTIFICATION_TITLE)
                    .build()
            )
        )
    }
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(appContext, GENERATE_FILE_CHANNEL)
            .setContentTitle(GENERATE_FILE_NOTIFICATION_TITLE)
            .setContentText(GENERATE_FILE_NOTIFICATION_DESCRIPTION)
            .setSmallIcon(R.drawable.baseline_file_present_24)
            .setOngoing(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

}