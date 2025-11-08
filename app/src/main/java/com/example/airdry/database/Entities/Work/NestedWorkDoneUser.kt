package com.example.airdry.database.Entities.Work

import androidx.room.Embedded
import androidx.room.Relation
import com.example.airdry.database.Entities.UserManagement.AppUser

data class NestedWorkDoneUser(
    @Embedded val workDoneUser: WorkDoneUser,
    @Relation(
        parentColumn = "work_done_id",
        entityColumn = "id"
    )
    val workDone: WorkDone,
    @Relation(
        parentColumn = "app_user_id",
        entityColumn = "id"
    )
    val appUser: AppUser
)

