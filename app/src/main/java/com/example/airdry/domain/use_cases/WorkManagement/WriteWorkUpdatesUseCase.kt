package com.example.airdry.domain.use_cases.WorkManagement

import android.util.Log
import com.example.airdry.data.dto.Work.CreateWorkDoneDTO
import com.example.airdry.domain.REST_API.interfaces.WorkApi
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.operational_states.ApiResponse
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.UUID
import javax.inject.Inject

class WriteWorkUpdatesUseCase @Inject constructor(
    private val workDoneRepository: WorkDoneRepository,
    private val workApi: WorkApi
) {

    operator fun invoke(): Flow<DownloadState> = flow {
        emit(DownloadState.Loading)

        val cachedUpdates = runCatching { getCachedWork()}
            .onFailure { e ->
                Log.d("WriteWorkUpdatesUseCase", "Failed to get cached updates: ${e.message}")
                emit(DownloadState.Error("Failed to load cached work updates: ${e.message}"))
            }
            .getOrElse { return@flow }

        if (cachedUpdates.isEmpty()) {
            Log.d("WriteWorkUpdatesUseCase", "No cached updates found")
            emit(DownloadState.Success("No work updates to sync"))
            return@flow
        }

        Log.d("WriteWorkUpdatesUseCase", "Starting upload for ${cachedUpdates.size} items")

        val uploadResults = coroutineScope {
            cachedUpdates.map { work ->
                async {
                    try {
                        Log.d("WriteWorkUpdatesUseCase", "Updating work ${work.id} with ${work.stacksList.size} stacks  size --${work.stacksList[0].stackIds.size} user_size -- ${work.workersList[0].userIds.size}")
                        when (val res = workApi.updateWork(work)) {
                            is ApiResponse.Success -> {
                                Log.d("WriteWorkUpdatesUseCase", "Work ${work.id} updated successfully")
                                workDoneRepository.unCacheWorkDoneForUpdate(work.id)
                                Result.success(work)
                            }
                            is ApiResponse.Created -> {
                                Log.d("WriteWorkUpdatesUseCase", "Work ${work.id} created (unexpected but accepted)")
                                Result.success(work)
                            }
                            is ApiResponse.Error -> {
                                Log.d("WriteWorkUpdatesUseCase", "Failed to update work ${work.id}: ${res.message}")
                                Result.failure(Exception(res.message))
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("WriteWorkUpdatesUseCase", "Exception updating work ${work.id}: ${e.message}")
                        Result.failure(e)
                    }
                }
            }.awaitAll()
        }

        val failed = uploadResults.filter { it.isFailure }
        if (failed.isEmpty()) {
            workDoneRepository.unCacheForUpdateAllWork()
            Log.d("WriteWorkUpdatesUseCase", "All updates uploaded successfully, cache cleared")
            emit(DownloadState.Success("All work updates uploaded successfully"))
        } else {
            val failedCount = failed.size
            Log.d("WriteWorkUpdatesUseCase", "$failedCount updates failed during upload")
            emit(
                DownloadState.Error(
                    "Partially uploaded: ${cachedUpdates.size - failedCount} succeeded, $failedCount failed"
                )
            )
        }

    }.catch { e ->
        Log.d("WriteWorkUpdatesUseCase", "Flow failed due to ${e.message}")
        emit(DownloadState.Error("Failed to write work updates: ${e.message}"))
    }

    private suspend fun getCachedWork(): List<CreateWorkDoneDTO> =
        runCatching {
            getCachedUpdateWorkAsDTO()
        }.onFailure {
            Log.d("getCachedForUpdate", "Failed to get cached work for update: ${it.message}")
        }.getOrElse { emptyList() }

    private  suspend fun getCachedUpdateWorkAsDTO(): List<CreateWorkDoneDTO> = withContext(Dispatchers.IO) {
        // Ensure Room flushes all pending inserts
        yield()
        delay(50)

        val workList = workDoneRepository.getPendingWorkDone()
        val result = mutableListOf<CreateWorkDoneDTO>()

        for (work in workList) {
            val userIds = workDoneRepository.getUserIdsForWorkDone(work.id)
            delay(500)
            val stackIds = workDoneRepository.getStackIdsForWorkDone(work.id)
            delay(500)
            Log.d("Transaction", "work ${work.id}")
            Log.d("Transaction", "users ${userIds.size} ${userIds}")
            Log.d("Transaction", "stacks ${stackIds.size} ${stackIds}")

            if (userIds.isNotEmpty() && stackIds.isNotEmpty()) {
                result.add(
                    CreateWorkDoneDTO(
                        id = work.id,
                        workDate = work.workDate,
                        workersList = listOf(CreateWorkDoneDTO.WorkDoneUsersDTO(UUID.randomUUID(), userIds)),
                        stacksList = listOf(CreateWorkDoneDTO.WorkDoneStacksDTO(UUID.randomUUID(), stackIds))
                    )
                )
            }
        }
        result
    }
}