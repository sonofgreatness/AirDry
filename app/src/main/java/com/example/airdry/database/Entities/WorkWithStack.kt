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
        val dimens = stack.type.height * stack.type.breadth * stack.type.width
        val sum =
            stack.type.numberOfPieces * workDone.amount * workDone.ownershipFraction
        return   dimens * sum

    }
}