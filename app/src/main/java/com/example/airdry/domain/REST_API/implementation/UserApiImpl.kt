package com.example.airdry.domain.REST_API.implementation

import android.util.Log
import com.example.airdry.auth.SessionExpiryRepository
import com.example.airdry.auth.SessionManager
import com.example.airdry.data.dto.Auth.AuthRequest
import com.example.airdry.data.dto.Auth.AuthResponse
import com.example.airdry.data.dto.Common.IdResponse
import com.example.airdry.data.dto.Common.PaginatedResponse
import com.example.airdry.data.dto.Common.SimpleResponse
import com.example.airdry.data.dto.User.UserRegisterRequest
import com.example.airdry.data.dto.User.UserResponse
import com.example.airdry.data.dto.User.UserTypesDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.ui.screens.auth.LoginScreen
import com.example.airdry.util.Constants.Companion.API_BASE_URL
import com.example.airdry.util.Constants.Companion.QUERY_PAGE
import com.example.airdry.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.airdry.util.Constants.Companion.QUERY_STRING
import com.example.airdry.util.Constants.Companion.SERVER_OK_TEXT
import com.example.airdry.util.operational_states.ApiResponse
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.API_HEALTH
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.API_URL_USERS_UNPAGED
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.API_URL_USERTYPES_GET_ALL
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.API_URL_USER_GET_ALL
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.API_URL_USER_GET_ALL_CASUALS
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.API_URL_USER_GET_FILTERED
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.API_URL_USER_LOGIN
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.API_URL_USER_REGISTER
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.PAGE
import com.example.airdry.util.operational_states.ConstanstFromShared.Companion.PAGE_SIZE
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.logging.Logger
import kotlin.time.Clock
import java.util.UUID
import kotlin.time.ExperimentalTime

class UserApiImpl(private val client: HttpClient,
                  private val sessionManager: SessionManager,
                  private val logger: Logger,
                  private  val sessionExpiryRepository: SessionExpiryRepository): UserApi {

    @OptIn(ExperimentalTime::class)
    override suspend fun login(username: String, password: String): Boolean {
        return try {
            Log.d("Login", "Before")
            val response = client.post("$API_BASE_URL$API_URL_USER_LOGIN") {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(username, password))
            }
            Log.d("Login","res : ${response.bodyAsText()}")
            val token = response.body<AuthResponse>()
            sessionManager.jwtToken = token.token
            Log.d("IMX", token.token)
            sessionManager.decodeJwtTokenPublic(token.token)
            sessionExpiryRepository.setLastLoginTimestamp(Clock.System.now().toEpochMilliseconds())
            true
        } catch (e: Exception) {
            Log.e("Login", "Error during login: ${e.message}", e)
            false
        }
    }

    override fun logout() {
        sessionManager.logout()
    }
    override suspend fun getUserTypes() :List<UserTypesDTO>{
        val response: List<UserTypesDTO> = client.get("$API_BASE_URL$API_URL_USERTYPES_GET_ALL") {
            contentType(ContentType.Application.Json)
        }.body()
        return response
    }
    override suspend  fun findById(id: UUID): UserRegisterRequest? {
        val response: UserRegisterRequest = client.get("$API_BASE_URL/user/$id/get_by_id") {
            contentType(ContentType.Application.Json)
        }.body()
        return response
    }
    override suspend fun findUserResponseById(id: UUID): UserResponse?{
        val response: UserResponse = client.get("$API_BASE_URL/user/$id/get_user_response_by_id") {
            contentType(ContentType.Application.Json)
        }.body()
        return response
    }
    override suspend fun getUsers(page: Int, pageSize: Int) :PaginatedResponse<UserResponse>?{
        val response : PaginatedResponse<UserResponse>? = client.get("$API_BASE_URL$API_URL_USER_GET_ALL"){
            contentType(ContentType.Application.Json)
        }.body()
        return response
    }
    override suspend fun createUser(user: UserRegisterRequest): ApiResponse {
        return try {
            val response: IdResponse = client.post("$API_BASE_URL$API_URL_USER_REGISTER") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }.body()
            ApiResponse.Created(response.id)
        } catch (e: Exception) {
            logger.error("Failed to create user: ${e.message}")
            ApiResponse.Error(code = 500, message = "Failed to create user: ${e.message}")
        }
    }
    override  suspend fun  getUsersUnPaged() : List<UserRegisterRequest>{
        val response :List<UserRegisterRequest> = client.get("$API_BASE_URL$API_URL_USERS_UNPAGED"){
            contentType(ContentType.Application.Json)
        }.body()
        return response
    }
    override suspend fun  getServerHealth(): SimpleResponse
    {
        try{
        val response = client.get("$API_BASE_URL$API_HEALTH"){
            contentType(ContentType.Application.Json)
        }
        return  if (response.status.isSuccess())
             SimpleResponse(message =  SERVER_OK_TEXT)
            else
             SimpleResponse(message = response.status.value.toString())
        }catch (e: Exception){
            Log.d("ConnectivityObserver","Server check failed ${e.message}")
            return SimpleResponse("Server check failed ${e.message}")
        }
    }
    override suspend fun updateUser(user: UserRegisterRequest): ApiResponse{
        try {
            val response =client.put("$API_BASE_URL/users/${user.id}/update") {
                contentType(ContentType.Application.Json)
                setBody(user.toUpdateUserDTO())
            }
            return if(response.status == HttpStatusCode.OK)
                ApiResponse.Success
            else
                ApiResponse.Error(code = response.status.value, message = "Failed to update user: ${response.bodyAsText()}")
        }catch (e: Exception){
            return ApiResponse.Error(code = 500, message = "Failed to update user: ${e.message}")
        }
    }
    override  suspend fun   deleteUserId(userId: UUID): ApiResponse {
        val response = client.delete("$API_BASE_URL/users/${userId}/delete") {
            contentType(ContentType.Application.Json)
        }
        return if(response.status == HttpStatusCode.OK)
            ApiResponse.Success
        else
            ApiResponse.Error(code = response.status.value, message = "Failed to delete user: ${response.bodyAsText()}")
    }

    override suspend fun getCasualUsers(
        page: Int,
        pageSize: Int,
        queryString: String?
    ): PaginatedResponse<UserResponse>? {
        val resp : PaginatedResponse<UserResponse>?     = client.get("$API_BASE_URL$API_URL_USER_GET_ALL_CASUALS"){
            url {
                parameters.append(QUERY_PAGE, page.toString())
                parameters.append(QUERY_PAGE_SIZE, pageSize.toString())
                queryString?.let { parameters.append(QUERY_STRING, it) }
            }
        }.body()
        return  resp
    }
    override  suspend  fun  getFilteredUsers (page: Int,
                                              pageSize: Int,filters: WorkDataFiltersDTO
    ) : PaginatedResponse<UserResponse>?{
        try {
            val resp = client.post("$API_BASE_URL$API_URL_USER_GET_FILTERED"){
                contentType(ContentType.Application.Json)
                url {
                    parameters.append(PAGE, page.toString())
                    parameters.append(PAGE_SIZE, pageSize.toString())
                }
                setBody(filters)
            }
            return resp.body()
        }catch (e: Exception){
            e.printStackTrace()
            return null
        }
    }
}