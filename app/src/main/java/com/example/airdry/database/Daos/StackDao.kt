package com.example.airdry.database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.Stack.StackCode
import java.util.UUID

@Dao
interface StackDao {
    @Insert
    suspend fun createStack(entity: Stack)
    @Insert
    suspend  fun createStackCode(entity: StackCode)
    @Query("DELETE FROM stack_code")
    suspend fun deleteAllStackCodesAndStack()
    @Transaction
    @Query("SELECT * FROM stack")
    suspend fun getAllNestedStacks(): List<NestedStack>?
    @Transaction
    @Query("SELECT * FROM stack WHERE id =:id")
    suspend fun getNestedStackById(id: UUID): List<NestedStack>?
}