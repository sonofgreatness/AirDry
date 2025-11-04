package com.example.airdry.data.dto.Organization

import kotlinx.serialization.Serializable
import org.montigny.client.dto.Common.UUIDSerializer
import java.util.UUID

@Serializable
data class CreateDptDTO(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val displayName: String,
    @Serializable(with = UUIDSerializer::class)
    val organizationId: UUID?=null)