package com.example.airdry.domain.workers.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.airdry.domain.workers.DownloadReleaseWorker
import com.example.airdry.domain.workers.GenerateExcelReportToExternalStorageWorker
import javax.inject.Inject

class MyWorkerFactory @Inject constructor(
    private val generateExcelFactory: GenerateExcelReportToExternalStorageWorker.Factory,
    private val downloadReleaseFactory : DownloadReleaseWorker.Factory
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            GenerateExcelReportToExternalStorageWorker::class.java.name ->
                generateExcelFactory.create(appContext, workerParameters)
            DownloadReleaseWorker::class.java.name ->
                downloadReleaseFactory.create(appContext, workerParameters)
            else -> null
        }
    }
}
