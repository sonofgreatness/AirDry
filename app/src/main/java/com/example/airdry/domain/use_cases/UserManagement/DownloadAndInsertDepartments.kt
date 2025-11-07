package com.example.airdry.domain.use_cases.UserManagement

import android.util.Log
import com.example.airdry.auth.SessionManager
import com.example.airdry.data.dto.Organization.CreateOrgDTO
import com.example.airdry.data.dto.User.UserResponse
import com.example.airdry.domain.REST_API.interfaces.OrganizationApi
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.Constants
import com.example.airdry.util.UtilityFunctions.Companion.toUUID
import com.example.airdry.util.operational_states.DownloadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DownloadAndInsertDepartments
    @Inject constructor(
        private val organizationApi: OrganizationApi,
        private val userRepository: UserRepository,
        private  val userApi: UserApi,
        private val sessionManager: SessionManager
    )
{
    private val _departments = MutableStateFlow<List<CreateOrgDTO>>(emptyList())
    operator fun invoke(): Flow<DownloadState> = flow {
        try {
            emit(DownloadState.Loading)
            _departments.value = emptyList()
            val userId = sessionManager.userId
            val user = userId?.let { userApi.findUserResponseById(it.toUUID()) }
            insertOrganization(user)
            fetchAllDepartments()
            //***
            userRepository.deleteAllUsers()
            //***
            _departments.value.forEach {
                var entity = it.toDepartment()
                user?.organization?.id.let { orgId ->
                    entity = entity.copy(organizationId = orgId)
                }
                userRepository.createDepartment(entity)
            }
            emit(DownloadState.Success(Constants.Companion.DOWNLOAD_DEPARTMENTS_DONE))
        } catch (e: Exception) {
            emit(DownloadState.Error("${Constants.Companion.DOWNLOAD_DEPARTMENTS_FAILED} ${e.message}"))
        }
    }
    private suspend fun fetchAllDepartments() {
        val res = organizationApi.getAllDept()
        res.let {
            _departments.value = it
        }
    }
   private suspend  fun  insertOrganization(user: UserResponse?){
      Log.d("org", "insert org")
       user?.organization?.id?.let {
           Log.d("org", "insert before")
           val org = organizationApi.getOrgById(it)
           userRepository.deleteAllOrganizations()
           userRepository.createOrganization(org.toOrganization())
           Log.d("org", "insert after")
       }
   }
}