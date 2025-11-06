package com.example.airdry.domain.use_cases

import com.example.airdry.data.dto.Organization.CreateSectDTO
import com.example.airdry.domain.REST_API.interfaces.OrganizationApi
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.Constants.Companion.DOWNLOAD_SECTIONS_DONE
import com.example.airdry.util.Constants.Companion.DOWNLOAD_SECTIONS_FAILED
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import  android.util.Log
class DownloadAndInsertSections
    @Inject constructor(
    private val organizationApi: OrganizationApi,
    private val userRepository: UserRepository){
    private val _sections = MutableStateFlow<List<CreateSectDTO>>(emptyList())
    suspend operator fun invoke():Flow<DownloadState> =flow{
        try{
            emit(DownloadState.Loading)
            _sections.value = emptyList()
            Log.d("yini", "kangaka")
            fetchAllSections()
            //***
            userRepository.deleteAllUsers()
            //***
            _sections.value.forEach {
                val entity = it.toSection()
                userRepository.createSection(entity)
            }
            emit(DownloadState.Success(DOWNLOAD_SECTIONS_DONE))
        }catch (e:Exception){

            emit(DownloadState.Error("$DOWNLOAD_SECTIONS_FAILED ${e.message}"))
        }
    }
    private suspend fun fetchAllSections() {
        Log.d("yini", "fetch all sections")
        val res = organizationApi.getAllSect()
        res.let {
            _sections.value = it
        }
    }
}