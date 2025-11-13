package com.example.airdry.domain.use_cases.UserManagement

import android.util.Log
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.operational_states.ApiResponse
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadCachedUsersUseCase @Inject constructor(
    private val userApi: UserApi,
    private val userRepository: UserRepository
) {

    private val _cachedUsers = MutableStateFlow<List<AppUser>>(emptyList())

    operator fun invoke(): Flow<DownloadState> = flow {
        emit(DownloadState.Loading)

        val cachedUsers = runCatching { fetchAllCachedUsers() }
            .getOrElse { e ->
                Log.d("UploadCachedUsersUseCase", "Failed to load cached users: ${e.message}")
                emit(DownloadState.Error("Failed to load cached users: ${e.message}"))
                return@flow
            }

        if (cachedUsers.isEmpty()) {
            Log.d("UploadCachedUsersUseCase", "No cached users found")
            emit(DownloadState.Success("No cached users to upload"))
            return@flow
        }

        Log.d("UploadCachedUsersUseCase", "Uploading ${cachedUsers.size} cached users")

        val uploadResults = coroutineScope {
            cachedUsers.map { user ->
                async {
                    try {
                        Log.d("UploadCachedUsersUseCase", "Uploading cached user: ${user.name}")
                        when (val resp = userApi.createUser(user.toUserRegisterRequest())) {
                            is ApiResponse.Created, ApiResponse.Success -> {
                                Log.d("UploadCachedUsersUseCase", "Uploaded cached user: ${user.name}")
                                // Mark user as synced
                                userRepository.editEmployee(user.copy(isPendingInsert = false))
                                Result.success(user)
                            }
                            is ApiResponse.Error -> {
                                Log.d(
                                    "UploadCachedUsersUseCase",
                                    "Failed to upload cached user: ${user.name} ${resp.message}"
                                )
                                Result.failure(Exception(resp.message))
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("UploadCachedUsersUseCase", "Exception uploading ${user.name}: ${e.message}")
                        Result.failure(e)
                    }
                }
            }.awaitAll()
        }

        val failed = uploadResults.filter { it.isFailure }
        if (failed.isEmpty()) {
            Log.d("UploadCachedUsersUseCase", "All cached users uploaded successfully")
            emit(DownloadState.Success("All cached users uploaded successfully"))
        } else {
            val failedCount = failed.size
            Log.d("UploadCachedUsersUseCase", "$failedCount user uploads failed")
            emit(
                DownloadState.Error(
                    "Partially uploaded: ${cachedUsers.size - failedCount} succeeded, $failedCount failed"
                )
            )
        }

    }.catch { e ->
        Log.d("UploadCachedUsersUseCase", "Flow failed due to ${e.message}")
        emit(DownloadState.Error("Failed to upload cached users: ${e.message}"))
    }

    private suspend fun fetchAllCachedUsers(): List<AppUser> =
        runCatching {
            val cached = userRepository.getAllCachedEmployees()
            _cachedUsers.value = cached?:emptyList()
            Log.d("UploadCachedUsersUseCase", "Fetched ${cached?.size} cached users")
            cached?:emptyList()
        }.onFailure {
            Log.d("UploadCachedUsersUseCase", "Failed to fetch cached users: ${it.message}")
        }.getOrElse { emptyList() }
}
