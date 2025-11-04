package com.example.airdry.util.operational_states

sealed class ApiResponse {
    object Success : ApiResponse()
    data class Created(val id: String) : ApiResponse()
    data class Error(val code: Int, val message: String) : ApiResponse()
}