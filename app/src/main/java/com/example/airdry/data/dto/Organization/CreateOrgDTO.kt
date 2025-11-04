package com.example.airdry.data.dto.Organization

import kotlinx.serialization.Serializable
import org.montigny.client.dto.Common.UUIDSerializer
import java.util.UUID

@Serializable
data class CreateOrgDTO(
    @Serializable(with = UUIDSerializer::class)
    val id : UUID? = UUID.randomUUID(),
    val displayName : String = ""
)