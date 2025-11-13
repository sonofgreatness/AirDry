package com.example.airdry.database.Entities.Work

import android.util.Log
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.airdry.data.dto.Work.CreateWorkDoneDTO
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.UserManagement.AppUser
import java.util.UUID

data class WorkDoneWithUsersAndStacks(
    @Embedded val workDone: WorkDone,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkDoneUser::class,
            parentColumn = "work_done_id",
            entityColumn = "app_user_id"
        )
    )
    val users: List<AppUser>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkDoneStack::class,
            parentColumn = "work_done_id",
            entityColumn = "stack_id"
        )
    )
    val stacks: List<Stack>
)
{
    fun generateCreateWorkDoneDTO() : CreateWorkDoneDTO{
        val workersList :List<UUID> = users.map {
         it.id
        }
        val userDto:CreateWorkDoneDTO.WorkDoneUsersDTO =CreateWorkDoneDTO.WorkDoneUsersDTO(
            id = UUID.randomUUID(),
            workersList)
        val stacksList :List<UUID> = stacks.map { it.id }
        val stacksDto : CreateWorkDoneDTO.WorkDoneStacksDTO = CreateWorkDoneDTO.WorkDoneStacksDTO(
            id = UUID.randomUUID(),
            stacksList
        )

        Log.d("w_2", "stacks pure ${stacksList.size}")
        Log.d("w_2", "users pure ${workersList.size}")
        Log.d("w_2", "stacks.size ${stacksList.size}")
        return CreateWorkDoneDTO(
            id = workDone.id,
            workDate = workDone.workDate,
            workersList = listOf(userDto),
            stacksList = listOf(stacksDto),
        )
    }
}



