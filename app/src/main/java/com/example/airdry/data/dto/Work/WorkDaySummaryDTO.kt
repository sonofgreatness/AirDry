package com.example.airdry.data.dto.Work
import com.example.airdry.database.Entities.Work.WorkDone
import com.example.airdry.database.Entities.Work.WorkDoneStack
import com.example.airdry.database.Entities.Work.WorkDoneUser
import com.example.airdry.util.UtilityFunctions.Companion.parseStringFromApiToTimeStamp
import com.example.airdry.util.UtilityFunctions.Companion.toUUIDOrNull
import kotlinx.serialization.Serializable
import org.montigny.client.dto.Common.UUIDNonNullableSerializer
import java.sql.Timestamp
import java.util.*

@Serializable
data class WorkDaySummaryDTO(
    val dayLabel: String,
    val totalVolume: Double,
    val totalStacks: Int,
    val totalUsers: Int,
    val workList: List<WorkSummaryDTO>
)

@Serializable
data class WorkSummaryDTO(
    @Serializable(with = UUIDNonNullableSerializer::class)
    val id: UUID,
    val workDate: String,
    val totalVolume: Double,
    val totalStacks: Int,
    val totalUsers: Int,
    val stacks: List<StackInfoDTO>,
    val users: List<UserInfoDTO>,
    val  capturedAt : String?=null,
    val updatedAt : String?=null,
    val  capturedBy: String?=null,
    val updatedBy: String?=null,
    ){
    fun getWorkDone(): WorkDone{

        return WorkDone(
            id = id,
            workDate = parseStringFromApiToTimeStamp(workDate),
            isPendingInsert = false,
            isPendingUpdate = false,
            capturedAt = parseNullableStringToTimestamp(capturedAt),
            capturedBy = extractFullNameFromApiString(capturedBy),
            updatedAt = parseNullableStringToTimestamp(updatedAt),
            updatedBy = extractFullNameFromApiString(updatedBy),
            captureById = extractUUIDFromApiString(capturedBy)
        )
    }

    private fun extractUUIDFromApiString(loaded: String?): UUID? {
        if (loaded.isNullOrBlank()) return null
        val uuidString = loaded.substringBefore(";").trim()
        return try {
            UUID.fromString(uuidString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    private fun extractFullNameFromApiString(loaded: String?): String {
        if (loaded.isNullOrBlank()) return ""
        return loaded.substringAfter(";", "").trim()
    }
    private fun parseNullableStringToTimestamp(dateString : String?) :Timestamp?{
        dateString?.let{
            return   parseStringFromApiToTimeStamp(it)
        }
        return null
    }
}

@Serializable
data class StackInfoDTO(
    @Serializable(with = UUIDNonNullableSerializer::class)
    val id: UUID,
    val displayName: String,
    val stackCodeDisplayName: String,
    val stackCode:String,
    val volume: Double
){
    fun generateWorkDoneStack(workDoneId: UUID) : WorkDoneStack{
        return WorkDoneStack(
            id = UUID.randomUUID() ,
            workDoneId = workDoneId,
            stackId = id)

    }
}


@Serializable
data class UserInfoDTO(
    @Serializable(with = UUIDNonNullableSerializer::class)
    val id: UUID,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val isActive : Boolean,
    val isApproved: Boolean,
    val personalCubes: Double
){
    fun generateWorkDoneUser(workDoneId: UUID) : WorkDoneUser{
        return WorkDoneUser(
           id = UUID.randomUUID(),
           workDoneId = workDoneId,
            appUserId =  id)
    }
}
