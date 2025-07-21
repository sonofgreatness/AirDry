package com.example.airdry.domain.repository.interfaces

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.database.Entities.WorkWithStack
import java.util.UUID

interface WorkDoneRepository {

    suspend fun createWork(workDone: WorkDone)
    suspend fun updateWork(workDone: WorkDone)
    suspend fun getWorkById(id: UUID): WorkDone?
    suspend fun getAllWork(): List<WorkDone>
    suspend fun getWorkByEmployeeId(employeeId: UUID): List<WorkWithStack>
}