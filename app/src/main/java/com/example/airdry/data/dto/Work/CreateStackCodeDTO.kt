package com.example.airdry.data.dto.Work

import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.Stack.StackCode
import kotlinx.serialization.Serializable
import org.montigny.client.dto.Common.UUIDSerializer
import java.util.UUID

@Serializable
data class CreateStackCodeDTO(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = UUID.randomUUID(),
    val displayCode: String? = null,
    val displayName: String? = null,
)
{
    fun toStackCode():StackCode{
        return StackCode(
            id = id?:UUID.randomUUID(),
            displayCode = displayCode?:"N/A",
            displayName = displayName?:"N/A",
        )

    }
}