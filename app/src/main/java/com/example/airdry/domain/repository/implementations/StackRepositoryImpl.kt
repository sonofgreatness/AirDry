package com.example.airdry.domain.repository.implementations

import com.example.airdry.database.Daos.StackDao
import com.example.airdry.database.Entities.Stack
import com.example.airdry.domain.repository.interfaces.StackRepository
import java.util.UUID
import javax.inject.Inject

class StackRepositoryImpl @Inject constructor(val stackDao: StackDao) : StackRepository  {
    override suspend fun createStack(stack: Stack) = stackDao.createStack(stack)
    override suspend fun editStack(stack: Stack) = stackDao.editStack(stack)
    override suspend fun getStackById(id: UUID) = stackDao.getStackById(id)
    override suspend fun getAllStacks() = stackDao.getAllStacks()


}