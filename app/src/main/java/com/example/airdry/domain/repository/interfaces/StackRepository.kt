package com.example.airdry.domain.repository.interfaces

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.Stack.StackCode
import java.util.UUID

interface StackRepository {
   suspend fun createStack(entity: Stack)
   suspend fun createStackCode(entity: StackCode)
   suspend fun deleteAllStackCodesAndStack()
   suspend fun getAllNestedStacks(): List<NestedStack>?
   suspend fun getNestedStackById(id: UUID): List<NestedStack>?
}
