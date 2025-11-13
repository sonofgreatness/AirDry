package com.example.airdry.domain.workers.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.airdry.domain.use_cases.UserManagement.UploadCachedUsersUseCase
import com.example.airdry.domain.use_cases.WorkManagement.UploadWorkDataUseCase
import com.example.airdry.domain.use_cases.WorkManagement.WriteWorkUpdatesUseCase
import com.example.airdry.domain.workers.UpdateWorkDataWorker
import com.example.airdry.domain.workers.UploadUsersWorker
import com.example.airdry.domain.workers.UploadWorkDataWorker
import javax.inject.Inject

class MyNewFactory @Inject constructor(
    private val uploadCachedUsersUseCase: UploadCachedUsersUseCase,
    private val uploadWorkDataUseCase: UploadWorkDataUseCase,
    private  val writeWorkUpdatesUseCase: WriteWorkUpdatesUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
             UploadUsersWorker::class.java.name ->
                UploadUsersWorker(appContext, workerParameters, uploadCachedUsersUseCase)
             UploadWorkDataWorker::class.java.name ->
                 UploadWorkDataWorker(appContext,workerParameters, uploadWorkDataUseCase)
            UpdateWorkDataWorker::class.java.name ->
                UpdateWorkDataWorker(appContext,workerParameters,writeWorkUpdatesUseCase)
            else -> null
        }
    }
}