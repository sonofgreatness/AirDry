package com.example.airdry.data.dto.Auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String,
                       val password: String)
