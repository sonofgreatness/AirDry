package com.example.airdry.database.Entities.UserManagement

import androidx.room.Embedded
import androidx.room.Relation

data class NestedAppUser (
    @Embedded val appUser: AppUser,
    @Relation(
        parentColumn = "department_id",
        entityColumn = "id"
    )
    val department: Department?,
    @Relation(
    parentColumn = "section_id",
    entityColumn = "id")
    val section : Section?)