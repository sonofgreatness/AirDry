package com.example.airdry.database.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.airdry.database.Entities.Stack
import java.util.UUID

@Dao
interface StackDao {
    @Insert
    suspend fun createStack(stack: Stack)
    @Update
    suspend fun editStack(stack: Stack)

    @Delete
    suspend fun deleteStack(stack: Stack)


    @Query("SELECT * FROM stack WHERE id = :id")
    suspend fun getStackById(id: UUID): Stack?
    @Query("SELECT * FROM stack")
    suspend fun getAllStacks(): List<Stack>



}