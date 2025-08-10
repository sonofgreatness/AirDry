package com.example.airdry.domain.use_cases

import android.app.Application
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.airdry.domain.workers.DownloadReleaseWorker
import com.example.airdry.domain.workers.GenerateExcelReportToExternalStorageWorker
import com.example.airdry.util.operational_states.WorkerReleaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadUpdateUseCase
@Inject constructor(
    private  val app : Application
){


    operator fun invoke(url : String): Flow<WorkerReleaseResponse> = flow {

            try {
                emit(WorkerReleaseResponse.Loading("Beginning download"))
                val workRequest =
                    OneTimeWorkRequestBuilder<DownloadReleaseWorker>()
                        .addTag("download update")
                        .setInputData(
                            workDataOf(
                                "url" to url
                            )
                        )
                        .build()
                val manager = WorkManager.getInstance(app.applicationContext)
                    .beginWith(workRequest)
                    .enqueue()
                emit(WorkerReleaseResponse.Loading("Downloading..."))
               if ( manager.result.isDone)
                   emit(WorkerReleaseResponse.Success("Download done"))

            } catch (ex: Exception) {
                Log.d(
                    "DownloadReleaseUseCase",
                    "failed to  write  to download release ${ex.localizedMessage}"
                )
                emit(WorkerReleaseResponse.Error(ex.message))
            }


        }

    }