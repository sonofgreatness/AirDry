package com.example.airdry.data.dto.Organization

import com.example.airdry.database.Entities.UserManagement.Department
import kotlinx.serialization.Serializable
import org.montigny.client.dto.Common.UUIDSerializer
import java.util.UUID

@Serializable
data class CreateOrgDTO(
    @Serializable(with = UUIDSerializer::class)
    val id : UUID? = UUID.randomUUID(),
    val displayName : String = ""
){
    fun toDepartment(): Department{
        return Department(
            id = id ?: UUID.randomUUID(),
            displayName = displayName,
            organizationId =null, createdAt = null,
            updatedAt = null, createdBy = null,
            updatedBy = null)
    }
}