package com.example.airdry.domain.repository.interfaces

import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.database.Entities.WorkWithStack
import java.time.LocalDate
import java.util.UUID

interface WorkDoneRepository {

    suspend fun createWork(workDone: WorkDone)
    suspend fun updateWork(workDone: WorkDone)
    suspend fun getWorkById(id: UUID): WorkDone?
    suspend fun getAllWork(): List<WorkDone>
    suspend fun getWorkByEmployeeId(employeeId: UUID): List<WorkWithStack>
    suspend fun getWorkWithStack(): List<WorkWithStack>
    suspend fun getWorkWithStackBetweenDates(startDate: String, endDate: String): List<WorkWithStack>



}