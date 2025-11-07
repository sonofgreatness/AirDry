package com.example.airdry.domain.repository.implementations

import com.example.airdry.database.Daos.StackDao
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.database.Entities.Stack.StackCode
import com.example.airdry.domain.repository.interfaces.StackRepository
import java.util.UUID
import javax.inject.Inject

class StackRepositoryImpl @Inject constructor(val stackDao: StackDao) : StackRepository  {

   override suspend fun createStack(entity: Stack) = stackDao.createStack(entity)
   override suspend fun createStackCode(entity: StackCode) = stackDao.createStackCode(entity)
   override suspend fun deleteAllStackCodesAndStack() = stackDao.deleteAllStackCodesAndStack()
   override suspend fun getAllNestedStacks(): List<NestedStack>? = stackDao.getAllNestedStacks()
   override suspend fun getNestedStackById(id: UUID): List<NestedStack>? =stackDao.getNestedStackById(id)
}