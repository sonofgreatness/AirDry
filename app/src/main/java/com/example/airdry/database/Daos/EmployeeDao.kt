package com.example.airdry.database.Daos
import androidx.room.*
import com.example.airdry.database.Entities.Employee
import java.util.UUID

@Dao
interface EmployeeDao {
    @Insert
    suspend fun createEmployee(entity: Employee)
    @Update
    suspend fun editEmployee(entity: Employee)
    @Query("""
        SELECT * FROM employee 
        
    """)
    suspend fun getAllEmployees() :List<Employee>


     @Query("SELECT * FROM employee WHERE id = :id LIMIT 1")
     suspend  fun getById(id: UUID): Employee?

}