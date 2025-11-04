package com.example.airdry.domain.REST_API.interfaces

import com.example.airdry.data.dto.Common.PaginatedResponse
import com.example.airdry.data.dto.Common.SimpleResponse
import com.example.airdry.data.dto.User.UserRegisterRequest
import com.example.airdry.data.dto.User.UserResponse
import com.example.airdry.data.dto.User.UserTypesDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import com.example.airdry.util.operational_states.ApiResponse
import java.util.UUID

interface UserApi {
    suspend fun login(username: String, password: String): Boolean
    fun logout()
    suspend fun getUserTypes():List<UserTypesDTO>
    suspend fun findById(id: UUID): UserRegisterRequest?
    suspend fun findUserResponseById(id: UUID): UserResponse?
    suspend fun getUsers(page: Int =DEFAULT_PAGE,
                         pageSize: Int= DEFAULT_PAGE_SIZE) :PaginatedResponse<UserResponse>?
    suspend fun createUser(user: UserRegisterRequest) : ApiResponse
    suspend fun  getUsersUnPaged() : List<UserRegisterRequest>
    suspend fun  getServerHealth(): SimpleResponse
    suspend fun  updateUser(user: UserRegisterRequest): ApiResponse
    suspend fun   deleteUserId(userId: UUID): ApiResponse
    suspend fun  getCasualUsers(page: Int,pageSize: Int, queryString : String? = null) :PaginatedResponse<UserResponse>?
    suspend fun  getFilteredUsers (
        page: Int,
        pageSize: Int,filters: WorkDataFiltersDTO
    ) : PaginatedResponse<UserResponse>?
}