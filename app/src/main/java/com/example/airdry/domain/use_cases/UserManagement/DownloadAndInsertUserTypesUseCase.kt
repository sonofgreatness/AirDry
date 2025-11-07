package com.example.airdry.domain.use_cases.UserManagement

import com.example.airdry.data.dto.User.UserTypesDTO
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.Constants
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadAndInsertUserTypesUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private  val userApi: UserApi
){
    private val _userTypes = MutableStateFlow<List<UserTypesDTO>>(emptyList())
    suspend operator fun invoke(): Flow<DownloadState> = flow {
        try {
            emit(DownloadState.Loading)
            _userTypes.value = emptyList()
            fetchAllUserTypes()
            //***
            userRepository.deleteAllUserTypes()
            //***
            _userTypes.value.forEach {
                val entity = it.toUserType()
                userRepository.createUserType(entity)
            }
            emit(DownloadState.Success(Constants.Companion.DOWNLOAD_USER_TYPES_DONE))
        } catch (e: Exception) {
            emit(DownloadState.Error("${Constants.Companion.DOWNLOAD_USER_TYPES_FAILED} ${e.message}"))
        }
    }
    private suspend fun fetchAllUserTypes() {
        val res = userApi.getUserTypes()
        res.let {
            _userTypes.value = it
        }
    }
}