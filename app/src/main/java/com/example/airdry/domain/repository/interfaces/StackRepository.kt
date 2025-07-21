package com.example.airdry.domain.repository.interfaces

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.airdry.database.Entities.Stack
import java.util.UUID

interface StackRepository {

    suspend fun createStack(stack: Stack)
    suspend fun editStack(stack: Stack)
    suspend fun getStackById(id: UUID): Stack?
    suspend fun getAllStacks(): List<Stack>

}
