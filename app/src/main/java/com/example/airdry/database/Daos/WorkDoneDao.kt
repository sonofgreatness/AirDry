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
import com.example.airdry.util.UtilityFunctions
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneOffset
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
        Log.d("nigga give it back", "entered getDailyWorkSummaries()")
        try {   Log.d("nigga give it back", "3")
            // STEP 1 — Load all WorkDoneWithUsersAndStacks (filtered)
            val filteredWorkDoneList = getFilteredWorkDone(filters)
            Log.d("nigga give it back", "3.ooo  filtered size  ${filteredWorkDoneList.size}")
            if (filteredWorkDoneList.isEmpty()) {
                return PaginatedResponse(emptyList(), page, pageSize, 0, 1)
            }
            // STEP 2 — Pagination manually (since Room doesn’t support LIMIT in relation queries)
            val totalCount = filteredWorkDoneList.size.toLong()
            val totalPages = if (totalCount == 0L) 1 else ((totalCount + pageSize - 1) / pageSize).toInt()
            val pagedList = filteredWorkDoneList.drop((page - 1) * pageSize).take(pageSize)
            // STEP 3 — Map to WorkSummaryDTO
            val summaries = pagedList.map { relation ->
                val workDone = relation.workDone
                val dateFmt = java.text.SimpleDateFormat(HUMAN_READABLE_FORMAT_TEXT)
                val workDateStr = dateFmt.format(java.util.Date(workDone.workDate.time))

                // stacks
                val stacks = relation.stacks.map { stack ->
                    val nestedStack = getNestedStackFromId(stack.id)
                    StackInfoDTO(
                        id = stack.id,
                        displayName = stack.displayName,
                        stackCodeDisplayName = nestedStack.stackCode.displayName ?: "",
                        stackCode = nestedStack.stackCode.displayCode ?: "",
                        volume = stack.volume?: 0.0
                    )
                }

                val totalVolume = stacks.sumOf { it.volume }
                val totalStacks = stacks.size

                // users
                val users = relation.users.map { user ->
                    UserInfoDTO(
                        id = user.id,
                        name = user.name.toString(),
                        surname = user.surname.toString(),
                        phoneNumber = user.phoneNumber.toString(),
                        isActive = user.isActive ==true,
                        isApproved = user.isApproved ==true,
                        personalCubes = 0.0 // will be filled later
                    )
                }
                val totalUsers = users.size
                val perUserVolume = if (totalUsers > 0) totalVolume / totalUsers else 0.0
                val usersWithCubes = users.map { it.copy(personalCubes = perUserVolume) }

                val timeFormat = java.text.SimpleDateFormat(HUMAN_READABLE_FORMAT_TEXT_HOUR_MINUTE)
                val createdAtStr = workDone.capturedAt?.let { timeFormat.format(java.util.Date(it.time)) }
                val updatedAtStr = workDone.updatedAt?.let { timeFormat.format(java.util.Date(it.time)) }


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
            // STEP 4 — Group by day
            val groupedByDay = summaries.groupBy { it.workDate }.map { (day, works) ->
                WorkDaySummaryDTO(
                    dayLabel = day,
                    totalVolume = works.sumOf { it.totalVolume },
                    totalStacks = works.sumOf { it.totalStacks },
                    totalUsers = works.flatMap { it.users }.distinctBy { it.id }.size,
                    workList = works
                )
            }
            Log.d("nigga give it back", "3.1")
            // STEP 5 — Return paginated response
            return PaginatedResponse(
                items = groupedByDay,
                page = page,
                pageSize = pageSize,
                totalCount = totalCount,
                totalPages = totalPages
            )

        }catch (e: Exception){
            Log.d("nigga give it back", " 2.9 failed due to ${e.message}")
            return null
        }


    }

    @Query("SELECT* FROM user_profile LIMIT 1")
    suspend  fun getAuthUser(): Profile?


    @Transaction
    suspend fun getFilteredWorkDone(filters: WorkDataFiltersDTO): List<WorkDoneWithUsersAndStacks> {
       val allWork = getAllWorkDoneWithUsersAndStack()
        Log.d("nigga give it back","3.2.0  filters : $filters")
        return allWork.filter { relation ->
            val work = relation.workDone
            // 1. Date filter
            val matchDate = filters.filterDate?.let { dateStr ->
                Log.d("nigga give it back","3.2 date  not null")
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


}