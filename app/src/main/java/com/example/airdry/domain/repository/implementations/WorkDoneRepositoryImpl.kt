package com.example.airdry.domain.repository.implementations

import com.example.airdry.data.dto.Common.PaginatedResponse
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
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
    override suspend fun getDailyWorkSummaries(
        page: Int,
        pageSize: Int,
        filters: WorkDataFiltersDTO
    ): PaginatedResponse<WorkDaySummaryDTO>? {
        return workDoneDao.getDailyWorkSummaries(page,pageSize,filters)}
override      suspend fun  getCachedWorkInsert():List<WorkDone>
= workDoneDao.getCachedWorkInsert()

   override suspend fun deleteWorkDoneChildren(workDoneId : UUID) = workDoneDao.deleteWorkDoneChildren(workDoneId)
   override suspend   fun deleteWorkById(workDoneId: UUID) = workDoneDao.deleteWorkById(workDoneId)
   override suspend fun unCacheForInsertAllWork() = workDoneDao.unCacheForInsertAllWork()
   override suspend fun getCachedUpdateWork():List<WorkDoneWithUsersAndStacks> = workDoneDao.getCachedUpdateWork()
   override suspend fun unCacheForUpdateAllWork() = workDoneDao.unCacheForUpdateAllWork()
   override suspend fun getPendingWorkDone(): List<WorkDone> = workDoneDao.getPendingWorkDone()
   override suspend fun getUserIdsForWorkDone(workDoneId: UUID): List<UUID> = workDoneDao.getUserIdsForWorkDone(workDoneId)
   override suspend fun getStackIdsForWorkDone(workDoneId: UUID): List<UUID> = workDoneDao.getStackIdsForWorkDone(workDoneId)
    override suspend fun unCacheWorkDoneForInsert(workDoneId: UUID) = workDoneDao.unCacheWorkDoneForInsert(workDoneId)
   override suspend fun unCacheWorkDoneForUpdate(workDoneId: UUID) = workDoneDao.unCacheWorkDoneForUpdate(workDoneId)
    override  suspend fun  getCachedWork():List<WorkDoneWithUsersAndStacks> = workDoneDao.getCachedWork()

   override suspend fun getCountOfWorkDonePendingInsert():Int = workDoneDao.getCountOfWorkDonePendingInsert()
   override suspend fun  getCountOfWorkDonePendingUpdate() :Int  = workDoneDao.getCountOfWorkDonePendingUpdate()
}