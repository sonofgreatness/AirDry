package com.example.airdry.domain.repository.implementations

import com.example.airdry.database.Daos.WorkDoneDao
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.database.Entities.WorkWithStack
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class WorkDoneRepositoryImpl
@Inject constructor(val workDoneDao: WorkDoneDao): WorkDoneRepository {
    override suspend fun createWork(workDone: WorkDone) = workDoneDao.createWork(workDone)
    override suspend fun updateWork(workDone: WorkDone) =workDoneDao.updateWork(workDone)
    override suspend fun getWorkById(id: UUID) = workDoneDao.getWorkById(id)
    override suspend fun getAllWork() = workDoneDao.getAllWork()
    override suspend fun getWorkByEmployeeId(employeeId: UUID) = workDoneDao.getWorkByEmployeeId(employeeId)
    override  suspend fun  getWorkWithStack() = workDoneDao.getWorkWithStack()
   override suspend fun getWorkWithStackBetweenDates(startDate: String, endDate: String)
   = workDoneDao.getWorkWithStackBetweenDates(startDate, endDate)


}