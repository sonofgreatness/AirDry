package com.example.airdry.data.dto.User


import com.example.airdry.database.Entities.UserManagement.AppUser
import kotlinx.serialization.Serializable
import org.montigny.client.dto.Common.UUIDSerializer
import java.util.UUID


@Serializable
data class UserResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val nationalId: String?,
    val email: String?,
    val isActive: Boolean,
    val isApproved: Boolean,

    // Resolved references
    val userType: NamedEntityResponse?,
    val organization: NamedEntityResponse?,
    val department: NamedEntityResponse?,
    val section: NamedEntityResponse?
){
    fun toAppUserFresh(): AppUser {
        return  AppUser(
            id = id ?: UUID.randomUUID(),
            name = name,
            surname = surname,
            nationalId = this@UserResponse.nationalId,
            phoneNumber = phoneNumber,
            isActive = isActive,
            isPendingInsert = false,
            isApproved = isApproved,
            createdAt = null,
            updatedAt = null,
            createdBy = null,
            updatedBy = null,
            userTypeId = userType?.id,
            organizationId = organization?.id,
            departmentId = department?.id,
            sectionId = section?.id,
            email = email.toString()
        )
    }
}

@Serializable
data class NamedEntityResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID?,
    val displayName: String
)
