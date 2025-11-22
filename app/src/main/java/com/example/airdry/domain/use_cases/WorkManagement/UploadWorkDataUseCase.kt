package com.example.airdry.domain.use_cases.WorkManagement

import com.example.airdry.data.dto.Work.CreateWorkDoneDTO
import com.example.airdry.domain.REST_API.interfaces.WorkApi
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import  android.util.Log
import com.example.airdry.util.operational_states.ApiResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch

class UploadWorkDataUseCase @Inject constructor(
    private val workDoneRepo : WorkDoneRepository,
    private val workApi: WorkApi
)  {

    operator fun invoke(): Flow<DownloadState> = flow {
        emit(DownloadState.Loading)
        Log.d("uploadwork ", "oo3.5")
        val cachedWork = runCatching { getCachedWork() }
            .getOrElse {
                Log.d("uploadwork ", "oo3.9")
                emit(DownloadState.Error("Failed to load cached work: ${it.message}"))
                return@flow
            }

        if (cachedWork.isEmpty()) {
            emit(DownloadState.Success("No work data to upload"))
            return@flow
        }

        // Upload concurrently
        val uploadResults = coroutineScope {
            cachedWork.map { work ->
                async {
                    when (val res = workApi.createWork(work)) {
                        is ApiResponse.Created ->{
                            workDoneRepo.unCacheWorkDoneForInsert(work.id)
                            Log.d("uploadwork ", "oo4.5")
                            Result.success(work)

                        }
                        is ApiResponse.Success ->{
                            workDoneRepo.unCacheWorkDoneForInsert(work.id)
                            Log.d("Transaction create", "success")
                            Result.success(work)

                        }
                        is ApiResponse.Error ->{
                            Log.d("Transaction create", "error")
                            Result.failure(Exception(res.message))}
                    }
                }
            }.awaitAll()
        }
        // Check for failures
        val failed = uploadResults.filter { it.isFailure }
        if (failed.isEmpty()) {
            // Only clear cache if ALL succeeded
            workDoneRepo.unCacheForInsertAllWork()
            emit(DownloadState.Success("All work data uploaded successfully"))
        } else {
            val failedCount = failed.size
            emit(
                DownloadState.Error(
                    "Uploaded partially: ${cachedWork.size - failedCount} succeeded, $failedCount failed"
                )
            )
        }

    }.catch { e ->
        emit(DownloadState.Error("Failed to upload work data: ${e.message}"))
    }

    private suspend fun getCachedWork(): List<CreateWorkDoneDTO> =
        runCatching {
            Log.d("uploadwork ", "oo4")
            workDoneRepo.getCachedWork().map { it.generateCreateWorkDoneDTO() }
        }.getOrElse {
            Log.e("getCachedWork", "Error fetching cached work: ${it.message}")
            emptyList()
        }
}