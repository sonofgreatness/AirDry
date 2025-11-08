package com.example.airdry.database.Daos

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.airdry.database.Entities.Work.WorkDone
import com.example.airdry.database.Entities.Work.WorkDoneStack
import com.example.airdry.database.Entities.Work.WorkDoneUser
import com.example.airdry.database.Entities.Work.WorkDoneWithUsersAndStacks
import java.sql.Timestamp
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

}