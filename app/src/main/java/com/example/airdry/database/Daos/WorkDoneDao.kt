package com.example.airdry.database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.database.Entities.WorkWithStack
import java.time.LocalDate
import java.util.UUID
@Dao
interface  WorkDoneDao {

    @Insert
    suspend fun createWork(workDone: WorkDone)

    @Update
    suspend fun updateWork(workDone: WorkDone)

    @Query("SELECT * FROM work_done WHERE id = :id")
    suspend fun getWorkById(id: UUID): WorkDone?

    @Query("SELECT * FROM work_done")
    suspend fun getAllWork(): List<WorkDone>

    @Transaction
    @Query("SELECT * FROM work_done WHERE employee_id = :employeeId")
    suspend fun getWorkByEmployeeId(employeeId: UUID): List<WorkWithStack>


    @Transaction
    @Query("SELECT * FROM work_done")
    suspend fun getWorkWithStack(): List<WorkWithStack>

    @Transaction
    @Query("""
    SELECT * FROM work_done 
    WHERE date_worked BETWEEN :startDate AND :endDate""")
    suspend fun getWorkWithStackBetweenDates(startDate: String, endDate: String): List<WorkWithStack>


}