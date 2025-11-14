package com.example.airdry.database.Daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.airdry.data.dto.Common.PaginatedResponse
import com.example.airdry.data.dto.Work.StackInfoDTO
import com.example.airdry.data.dto.Work.UserInfoDTO
import com.example.airdry.data.dto.Work.WorkDaySummaryDTO
import com.example.airdry.data.dto.Work.WorkSummaryDTO
import com.example.airdry.data.dto.WorkDataFiltersDTO
import com.example.airdry.database.Entities.Stack.NestedStack
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.database.Entities.Work.WorkDone
import com.example.airdry.database.Entities.Work.WorkDoneStack
import com.example.airdry.database.Entities.Work.WorkDoneUser
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE
import com.example.airdry.util.Constants.Companion.DEFAULT_PAGE_SIZE
import com.example.airdry.util.Constants.Companion.HUMAN_READABLE_FORMAT_TEXT
import com.example.airdry.util.Constants.Companion.HUMAN_READABLE_FORMAT_TEXT_HOUR_MINUTE
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import java.util.UUID

@Dao
interface  WorkDoneDao {

    @Insert
    suspend fun createWork(workDone: WorkDone)
    @Update
    suspend fun updateWork(workDone: WorkDone)
    @Delete
    suspend fun deleteWork(workDone: WorkDone)
    @Query("DELETE FROM work_done")
    suspend fun deleteAllWorkDone()

    @Query("DELETE FROM work_done_user")
    suspend fun deleteAllWorkDoneUser()

    @Query("DELETE FROM work_done_stack")
    suspend fun deleteAllWorkDoneStack()

  @Transaction
    suspend fun deleteAllWork() {
    try {
        deleteAllWorkDone()
    }
    catch (e: Exception){
        Log.d("challenge", "failed due to ${e.message}")
    }
       // deleteAllWorkDoneUser()
        //deleteAllWorkDoneStack()
    }
    @Insert
    suspend fun createWorkDoneUser(entity: WorkDoneUser)
    @Update
    suspend fun updateWorkDoneUser(entity: WorkDoneUser)
    @Insert
    suspend fun  createWorkDoneStack(entity: WorkDoneStack)
    @Update
    suspend fun updateWorkDoneStack(entity: WorkDoneStack)

    @Query("SELECT * FROM work_done WHERE id = :id")
    suspend fun getWorkDoneWithUsersAndStacksById(id: UUID): WorkDoneWithUsersAndStacks?
     @Transaction
     @Query("SELECT * FROM work_done")
     suspend fun  getAllWorkDoneWithUsersAndStack():List<WorkDoneWithUsersAndStacks>

     @Query("SELECT * FROM work_done WHERE is_pending_insert = 1")
     suspend fun  getCachedWorkInsert():List<WorkDone>

    @Transaction
    @Query("SELECT * FROM work_done WHERE work_date BETWEEN :startDate AND :endDate")
    suspend fun  getAllWorkDoneWithUsersAndStackBetweenDates(
        startDate: Timestamp,
        endDate: Timestamp
    ):List<WorkDoneWithUsersAndStacks>

    @Transaction
    @Query("""
    SELECT DISTINCT work_done.* 
    FROM work_done
    INNER JOIN work_done_user 
        ON work_done.id = work_done_user.work_done_id
    WHERE work_done_user.app_user_id = :userId""")
    suspend fun getWorkDoneWithUsersAndStacksByUserId(
        userId: UUID
    ): List<WorkDoneWithUsersAndStacks>

    @Transaction
    suspend fun getDailyWorkSummaries(
        page: Int = DEFAULT_PAGE,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        filters: WorkDataFiltersDTO
    ): PaginatedResponse<WorkDaySummaryDTO>? {
        try {
            val filteredWorkDoneList = getFilteredWorkDone(filters)
            return paginatedResponse(filteredWorkDoneList, page, pageSize)

        }catch (e: Exception){
            Log.d("getDailyWorkSummaries", "DAO failed due to ${e.message}")
            return null
        }
    }

    @Transaction
    suspend fun paginatedResponse(
        filteredWorkDoneList: List<WorkDoneWithUsersAndStacks>,
        page: Int,
        pageSize: Int
    ): PaginatedResponse<WorkDaySummaryDTO> {
        if (filteredWorkDoneList.isEmpty()) {
            // Return an empty paginated response if there are no records
            return PaginatedResponse(emptyList(), page, pageSize, 0, 1)
        }

        // 2. Map all filtered work done records to atomic WorkSummaryDTOs
        val summaries = filteredWorkDoneList.map { relation ->
            val workDone = relation.workDone
            val dateFmt = SimpleDateFormat(HUMAN_READABLE_FORMAT_TEXT,Locale.getDefault())
            val workDateStr = dateFmt.format(Date(workDone.workDate.time))

            // ... (Stack mapping logic remains the same)
            val stacks = relation.stacks.map { stack ->
                val nestedStack = getNestedStackFromId(stack.id)
                StackInfoDTO(
                    id = stack.id,
                    displayName = stack.displayName,
                    stackCodeDisplayName = nestedStack.stackCode.displayName ?: "",
                    stackCode = nestedStack.stackCode.displayCode ?: "",
                    volume = stack.volume ?: 0.0
                )
            }
            val totalVolume = stacks.sumOf { it.volume }
            val totalStacks = stacks.size

            // ... (User mapping and volume calculation logic remains the same)
            val users = relation.users.map { user ->
                UserInfoDTO(
                    id = user.id,
                    name = user.name.toString(),
                    surname = user.surname.toString(),
                    phoneNumber = user.phoneNumber.toString(),
                    isActive = user.isActive == true,
                    isApproved = user.isApproved == true,
                    personalCubes = 0.0
                )
            }
            val totalUsers = users.size
            val perUserVolume = if (totalUsers > 0) totalVolume / totalUsers else 0.0
            val usersWithCubes = users.map { it.copy(personalCubes = perUserVolume) }

            val timeFormat = SimpleDateFormat(HUMAN_READABLE_FORMAT_TEXT_HOUR_MINUTE, Locale.getDefault())
            val createdAtStr = workDone.capturedAt?.let { timeFormat.format(Date(it.time)) }
            val updatedAtStr = workDone.updatedAt?.let { timeFormat.format(Date(it.time)) }

            WorkSummaryDTO(
                id = workDone.id,
                workDate = workDateStr,
                totalVolume = totalVolume,
                totalStacks = totalStacks,
                totalUsers = totalUsers,
                stacks = stacks,
                users = usersWithCubes,
                capturedAt = createdAtStr,
                updatedAt = updatedAtStr,
                capturedBy = workDone.capturedBy.toString(),
                updatedBy = workDone.updatedBy.toString()
            )
        }

        // 3. GROUP by day: Convert atomic summaries into final WorkDaySummaryDTOs
        // This is the list we need to paginate.
        val groupedByDay = summaries.groupBy { it.workDate }.map { (day, works) ->
            WorkDaySummaryDTO(
                dayLabel = day,
                totalVolume = works.sumOf { it.totalVolume },
                totalStacks = works.sumOf { it.totalStacks },
                totalUsers = works.flatMap { it.users }.distinctBy { it.id }.size,
                workList = works
            )
        }.sortedByDescending {
            // Optional: Sort by date descending to show most recent first
            SimpleDateFormat(HUMAN_READABLE_FORMAT_TEXT, Locale.getDefault()).parse(it.dayLabel)
        }

        // 4. APPLY PAGINATION to the grouped list
        val finalCount = groupedByDay.size.toLong() // Total count of *DAYS*
        val finalTotalPages =
            if (finalCount == 0L) 1 else ((finalCount + pageSize - 1) / pageSize).toInt()

        val pagedGroupedList = groupedByDay
            .drop((page - 1) * pageSize)
            .take(pageSize)

        // 5. Return the response
        return PaginatedResponse(
            items = pagedGroupedList, // The paged list of daily summaries
            page = page,
            pageSize = pageSize,
            totalCount = finalCount, // The total number of unique days
            totalPages = finalTotalPages
        )
    }






    @Query("SELECT* FROM user_profile LIMIT 1")
    suspend  fun getAuthUser(): Profile?


    @Transaction
    suspend fun getFilteredWorkDone(filters: WorkDataFiltersDTO): List<WorkDoneWithUsersAndStacks> {
       val allWork = getAllWorkDoneWithUsersAndStack()
        return allWork.filter { relation ->
            val work = relation.workDone
            // 1. Date filter
            val matchDate = filters.filterDate?.let { dateStr ->
                val date = LocalDate.parse(dateStr)
                val startOfDay = date.minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
                val endOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant()
                work.workDate.toInstant() in startOfDay..endOfDay
            } ?: true

            // 2. Department, section, and user filters
            val matchUser = relation.users.any { user ->
                var ok = true
                if (!filters.includeInactiveWorkers) {
                    ok = ok && (user.isActive == true && user.isApproved == true)
                }
                filters.filterDepartmentId?.let { ok = ok && user.departmentId == it }
                filters.filterSectionId?.let { ok = ok && user.sectionId == it }
                filters.filterName?.takeIf { it.isNotBlank() }?.let {
                    ok = ok && user.name?.contains(it, ignoreCase = true) == true
                }
                filters.filterSurname?.takeIf { it.isNotBlank() }?.let {
                    ok = ok && user.surname?.contains(it, ignoreCase = true) == true
                }
                filters.filterPhoneNumber?.takeIf { it.isNotBlank() }?.let {
                    ok = ok && user.phoneNumber?.contains(it) == true
                }
                filters.includeOtherStaffRecords?.let{
                    if(it == false){
                        val profile = getAuthUser()
                        profile?.let { profile ->
                            ok = ok && checkIfWorkDoneBelongsToUser(relation.workDone.captureById, profile)
                        }
                    }
                   }
                ok
            }
            matchDate && matchUser
        }
    }
    @Transaction
    suspend fun getDailyWorkSummariesForUser(
        page: Int = DEFAULT_PAGE,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        filters: WorkDataFiltersDTO
    ): PaginatedResponse<WorkDaySummaryDTO>? {
        try {
            val filteredWorkDoneList = getFilteredWorkDoneForUser(filters)
            return paginatedResponse(filteredWorkDoneList, page, pageSize)

        }catch (e: Exception){
            Log.d("getDailyWorkSummaries", "DAO failed due to ${e.message}")
            return null
        }
    }

    @Transaction
    suspend fun getFilteredWorkDoneForUser(filters: WorkDataFiltersDTO): List<WorkDoneWithUsersAndStacks> {
        val allWork = getAllWorkDoneWithUsersAndStack()
        return allWork.filter { relation ->
            val work = relation.workDone
            // 1. Date filter
            val matchDate = filters.filterDate?.let { dateStr ->
                val date = LocalDate.parse(dateStr)
                val startOfDay = date.minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant()
                val endOfDay = date.atStartOfDay(ZoneOffset.UTC).toInstant()
                work.workDate.toInstant() in startOfDay..endOfDay
            } ?: true

            // 2. Department, section, and user filters
            val matchUser = relation.users.any { user ->
                var ok = true
                if (!filters.includeInactiveWorkers) {
                    ok = ok && (user.isActive == true && user.isApproved == true)
                }
                filters.filterUserId?.let { ok = ok && user.id == it }
                ok
            }
            matchDate && matchUser
        }
    }


    fun checkIfWorkDoneBelongsToUser(
        uUID: UUID?,
        profile: Profile
    ):Boolean {
        if (uUID == null) return false
        return uUID == profile.id
    }

    @Query("SELECT * FROM stack WHERE id = :id LIMIT 1")
    suspend fun getNestedStackFromId(id: UUID) : NestedStack
    @Query("SELECT *FROM app_user where id =:id LIMIT 1")
    suspend fun getNestedAppUserFromId(id: UUID) : NestedAppUser
    @Query("DELETE FROM work_done_user WHERE work_done_id = :workDoneId")
    suspend fun deleteWorkDoneUsersById(workDoneId: UUID)
    @Query("DELETE FROM work_done_stack WHERE work_done_id = :workDoneId")
    suspend fun deleteWorkDoneStacksById(workDoneId: UUID)
    @Transaction
    suspend fun deleteWorkDoneChildren(workDoneId : UUID){
        deleteWorkDoneUsersById(workDoneId)
        deleteWorkDoneStacksById(workDoneId)
    }

    @Query("DELETE  FROM work_done  WHERE id =:workDoneId")
    suspend  fun deleteWorkById(workDoneId: UUID)
    @Transaction
    suspend fun unCacheForInsertAllWork(){
        val cached = getCachedWorkInsert()
        cached.forEach {
            updateWork(it.copy(isPendingInsert = false))
        }
    }
    @Transaction
    @Query("""SELECT * FROM work_done WHERE is_pending_update = 1
""")
    suspend fun getCachedUpdateWork():List<WorkDoneWithUsersAndStacks>


    @Query("SELECT * FROM work_done WHERE is_pending_update = 1")
    suspend fun getPendingWorkDone(): List<WorkDone>


    @Query("SELECT app_user_id FROM work_done_user WHERE work_done_id = :workDoneId")
    suspend fun getUserIdsForWorkDone(workDoneId: UUID): List<UUID>

    @Query("SELECT stack_id FROM work_done_stack WHERE work_done_id = :workDoneId")
    suspend fun getStackIdsForWorkDone(workDoneId: UUID): List<UUID>




    @Transaction
    suspend fun unCacheForUpdateAllWork(){
        val  cached = getCachedUpdateWork()
        cached.forEach {
            updateWork(it.workDone.copy(isPendingUpdate = false))
        }
    }

    @Query("UPDATE work_done SET is_pending_insert =0 WHERE id =:workDoneId")
    suspend fun unCacheWorkDoneForInsert(workDoneId: UUID)

    @Query("UPDATE work_done SET is_pending_update =0 WHERE id =:workDoneId")
    suspend fun unCacheWorkDoneForUpdate(workDoneId: UUID)

    @Query("SELECT * FROM work_done WHERE is_pending_insert = 1")
    suspend fun  getCachedWork():List<WorkDoneWithUsersAndStacks>

    @Query("SELECT COUNT(*) FROM work_done WHERE is_pending_insert = 1")
    suspend fun getCountOfWorkDonePendingInsert() :Int
    @Query("SELECT COUNT(*) FROM work_done WHERE is_pending_update = 1")
    suspend fun  getCountOfWorkDonePendingUpdate() : Int
}







