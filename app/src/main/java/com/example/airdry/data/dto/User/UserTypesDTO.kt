package com.example.airdry.data.dto.User

import com.example.airdry.database.Entities.UserManagement.UserType
import kotlinx.serialization.Serializable
import org.montigny.client.dto.Common.UUIDSerializer
import java.util.UUID

@Serializable
data class UserTypesDTO(
    @Serializable(with = UUIDSerializer::class)
    val id : UUID? = null,
    val displayName: String
) {
    fun toUserType() : UserType{
        return UserType(
            id = id?: UUID.randomUUID(),
            displayName = displayName,
            createdAt = null,
            updatedAt = null,
            createdBy = null,
            updatedBy = null
        )
    }
}