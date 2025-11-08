package com.example.airdry.database.Entities.Work

import androidx.room.Embedded
import androidx.room.Relation
import com.example.airdry.database.Entities.Stack.Stack

data class NestedWorkDoneStack(
    @Embedded val workDoneStack: WorkDoneStack,
    @Relation(
        parentColumn = "work_done_id",
        entityColumn = "id"
    )
    val workDone: WorkDone,
    @Relation(
        parentColumn = "stack_id",
        entityColumn = "id"
    )
    val stack: Stack
)
