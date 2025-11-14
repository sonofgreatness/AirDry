package com.example.airdry.domain.repository.interfaces

import androidx.room.Query
import com.example.airdry.data.dto.Common.PaginatedResponse
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.database.Entities.Work.WorkDone
import com.example.airdry.database.Entities.Work.WorkDoneStack
import com.example.airdry.database.Entities.Work.WorkDoneUser
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import java.sql.Timestamp
import java.util.UUID

interface WorkDoneRepository {

    suspend fun createWork(workDone: WorkDone)
    suspend fun updateWork(workDone: WorkDone)
    suspend fun deleteWork(workDone: WorkDone)
    suspend fun deleteAllWork()
    suspend fun createWorkDoneUser(entity: WorkDoneUser)
    suspend fun updateWorkDoneUser(entity: WorkDoneUser)
    suspend fun  createWorkDoneStack(entity: WorkDoneStack)
    suspend fun updateWorkDoneStack(entity: WorkDoneStack)
    suspend fun getWorkDoneWithUsersAndStacksById(id: UUID): WorkDoneWithUsersAndStacks?
    suspend fun  getAllWorkDoneWithUsersAndStack():List<WorkDoneWithUsersAndStacks>
    suspend fun  getAllWorkDoneWithUsersAndStackBetweenDates(
        startDate: Timestamp,
        endDate: Timestamp
    ):List<WorkDoneWithUsersAndStacks>
    suspend fun getWorkDoneWithUsersAndStacksByUserId(
        userId: UUID
    ): List<WorkDoneWithUsersAndStacks>
    suspend fun getDailyWorkSummaries(
        page: Int = DEFAULT_PAGE,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        filters: WorkDataFiltersDTO
    ): PaginatedResponse<WorkDaySummaryDTO>?
    suspend fun  getCachedWorkInsert():List<WorkDone>
    suspend fun deleteWorkDoneChildren(workDoneId : UUID)
    suspend  fun deleteWorkById(workDoneId: UUID)
    suspend fun unCacheForInsertAllWork()
    suspend fun getCachedUpdateWork():List<WorkDoneWithUsersAndStacks>
    suspend fun unCacheForUpdateAllWork()

    suspend fun getPendingWorkDone(): List<WorkDone>
    suspend fun getUserIdsForWorkDone(workDoneId: UUID): List<UUID>
    suspend fun getStackIdsForWorkDone(workDoneId: UUID): List<UUID>
    suspend fun unCacheWorkDoneForInsert(workDoneId: UUID)
    suspend fun unCacheWorkDoneForUpdate(workDoneId: UUID)
    suspend fun  getCachedWork():List<WorkDoneWithUsersAndStacks>

    suspend fun getCountOfWorkDonePendingInsert() :Int
    suspend fun  getCountOfWorkDonePendingUpdate() :Int
    suspend fun getDailyWorkSummariesForUser(
        page: Int = DEFAULT_PAGE,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        filters: WorkDataFiltersDTO
    ): PaginatedResponse<WorkDaySummaryDTO>?
}