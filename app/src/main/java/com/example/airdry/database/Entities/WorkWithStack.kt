package com.example.airdry.database.Entities

import androidx.room.Embedded
import androidx.room.Relation

data class WorkWithStack(
    @Embedded val workDone: WorkDone,

    @Relation(
        parentColumn = "stack_id",
        entityColumn = "id"
    )
    val stack: Stack
){
    fun getCubes (): Double{

        val sum = workDone.amount * workDone.ownershipFraction
        return   stack.getStackCubes() * sum

    }
}