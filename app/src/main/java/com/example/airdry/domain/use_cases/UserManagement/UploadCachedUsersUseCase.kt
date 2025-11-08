package com.example.airdry.domain.use_cases.UserManagement

import android.util.Log
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.operational_states.ApiResponse
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadCachedUsersUseCase @Inject constructor(
    private val userApi: UserApi,
    private val userRepository: UserRepository
)

{
    private val _cachedUsers = MutableStateFlow<List<AppUser>?>(emptyList())
    operator fun invoke(): Flow<DownloadState> = flow {
        try {
            emit(DownloadState.Loading)
            fetchAllCachedUsers()
            _cachedUsers.value?.forEach {
                Log.d("forcedUpload", "Uploading cached user: ${it.name}")
                val resp = userApi.createUser(it.toUserRegisterRequest())
                when (resp) {
                    is ApiResponse.Created -> {
                        Log.d("forcedUpload", "Uploaded cached user: ${it.name}")
                    }

                    is ApiResponse.Error -> {
                        Log.d(
                            "forcedUpload",
                            "Failed to upload cached user: ${it.name} ${resp.message}"
                        )
                    }

                    ApiResponse.Success -> {
                        Log.d("forcedUpload", "Uploaded cached user: ${it.name}")
                        userRepository.editEmployee(it.copy(isPendingInsert = false))
                    }
                }

            }
            emit(DownloadState.Success("Uploaded cached users successfully"))
        } catch (e: Exception) {
            emit(DownloadState.Error(message = "Failed to upload cached users ${e.message}"))
        }
    }
    private suspend fun   fetchAllCachedUsers(){
        _cachedUsers.value = userRepository.getAllCachedEmployees()}
}