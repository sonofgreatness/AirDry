package com.example.airdry.domain.repository.implementations

import com.example.airdry.database.Daos.WorkDoneDao
import com.example.airdry.database.Entities.Work.WorkDone
import com.example.airdry.database.Entities.Work.WorkDoneStack
import com.example.airdry.database.Entities.Work.WorkDoneUser
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import java.sql.Timestamp
import java.util.UUID
import javax.inject.Inject

class WorkDoneRepositoryImpl
@Inject constructor(val workDoneDao: WorkDoneDao): WorkDoneRepository {
   override suspend fun createWork(workDone: WorkDone) =
       workDoneDao.createWork(workDone)

    override suspend fun updateWork(workDone: WorkDone) =
        workDoneDao.updateWork(workDone)

    override suspend fun deleteWork(workDone: WorkDone) =
        workDoneDao.deleteWork(workDone)

    override suspend fun deleteAllWork() =
        workDoneDao.deleteAllWork()
    override suspend fun createWorkDoneUser(entity: WorkDoneUser)
     = workDoneDao.createWorkDoneUser(entity)
    override suspend fun updateWorkDoneUser(entity: WorkDoneUser)=
        workDoneDao.updateWorkDoneUser(entity)
    override suspend fun  createWorkDoneStack(entity: WorkDoneStack)=
        workDoneDao.createWorkDoneStack(entity)
    override suspend fun updateWorkDoneStack(entity: WorkDoneStack)
    =workDoneDao.updateWorkDoneStack(entity)
    override suspend fun getWorkDoneWithUsersAndStacksById(id: UUID): WorkDoneWithUsersAndStacks?
    =workDoneDao.getWorkDoneWithUsersAndStacksById(id)

    override suspend fun  getAllWorkDoneWithUsersAndStack():List<WorkDoneWithUsersAndStacks>
    =workDoneDao.getAllWorkDoneWithUsersAndStack()

    override suspend fun  getAllWorkDoneWithUsersAndStackBetweenDates(
        startDate: Timestamp,
        endDate: Timestamp
    ):List<WorkDoneWithUsersAndStacks>
    =workDoneDao.getAllWorkDoneWithUsersAndStackBetweenDates(startDate,endDate)

    override suspend fun getWorkDoneWithUsersAndStacksByUserId(
        userId: UUID
    ): List<WorkDoneWithUsersAndStacks> =
        workDoneDao.getWorkDoneWithUsersAndStacksByUserId(userId)

}