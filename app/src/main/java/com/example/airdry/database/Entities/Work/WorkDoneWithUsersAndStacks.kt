package com.example.airdry.database.Entities.Work

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.UserManagement.AppUser

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
