package com.example.airdry.domain.use_cases

import android.app.Application
import com.example.airdry.data.dto.User.UserResponse
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE_BIG
import com.example.airdry.util.Constants.Companion.DOWNLOAD_USERS_DONE
import com.example.airdry.util.Constants.Companion.DOWNLOAD_USERS_FAILED
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadAndInserCasualUsers @Inject constructor(
    private val userApi: UserApi,
    private val userRepository: UserRepository
)
{
    private val _currentPage = MutableStateFlow(1)
    private val _totalPages = MutableStateFlow(Int.MAX_VALUE)
    private val _casualWorkers = MutableStateFlow<List<UserResponse>>(emptyList())
    suspend operator fun invoke():Flow<DownloadState> =flow{
        try{
            emit(DownloadState.Loading)
            _currentPage.value = 1
            _totalPages.value = Int.MAX_VALUE
            _casualWorkers.value = emptyList()
            fetchAllCasualWorkers()
            //***
            userRepository.deleteAllUsers()
            //***
            _casualWorkers.value.forEach {
                val entity = it.toAppUserFresh()
                userRepository.createEmployee(entity)
            }
            emit(DownloadState.Success(DOWNLOAD_USERS_DONE))
        }catch (e:Exception){

          emit(DownloadState.Error("$DOWNLOAD_USERS_FAILED ${e.message}"))
        }
    }

    private suspend fun fetchAllCasualWorkers() {
        val page = _currentPage.value
        if (page > _totalPages.value) return
        val res = userApi.getCasualUsers(page, DEFAULT_PAGE_SIZE_BIG)
        res?.let {
            if (page <= 1) {
                _casualWorkers.value = it.items
                _totalPages.value = it.totalPages
            } else {
                _casualWorkers.value += it.items
            }
        }
        _currentPage.value++
        fetchAllCasualWorkers()
    }

}