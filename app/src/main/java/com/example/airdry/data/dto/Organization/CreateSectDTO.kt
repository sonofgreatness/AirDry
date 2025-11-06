package com.example.airdry.data.dto.Organization

import com.example.airdry.database.Entities.UserManagement.Section
import kotlinx.serialization.Serializable
import org.montigny.client.dto.Common.UUIDSerializer
import java.util.UUID
@Serializable
data class CreateSectDTO (
    @Serializable(with = UUIDSerializer::class)
    val id: UUID?,
    val displayName: String,
    @Serializable(with = UUIDSerializer::class)
    val departmentId: UUID?){
    fun toSection(): Section{
        return  Section(
            id =id?:UUID.randomUUID(),
            displayName = displayName,
            departmentId = departmentId?:UUID.randomUUID(),
            createdAt = null, updatedAt = null,
            createdBy = null,
            updatedBy = null)

    }
}