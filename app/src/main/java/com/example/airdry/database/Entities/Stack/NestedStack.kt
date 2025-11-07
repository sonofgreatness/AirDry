package com.example.airdry.database.Entities.Stack

import androidx.room.Embedded
import androidx.room.Relation
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.Stack.StackCode

data class NestedStack(
    @Embedded val stack: Stack,
    @Relation(
        parentColumn = "stack_code_id",
        entityColumn = "id"
    )
    val stackCode: StackCode
)