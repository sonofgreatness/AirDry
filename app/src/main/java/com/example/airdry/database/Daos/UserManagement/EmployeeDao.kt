package com.example.airdry.database.Daos.UserManagement

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Profile
import java.util.UUID

@Dao
interface EmployeeDao {
    @Insert
    suspend fun createEmployee(entity: AppUser)
    @Update
    suspend fun editEmployee(entity: AppUser)
    @Query("""
        SELECT * FROM  app_user WHERE is_active =1
        
    """)
    suspend fun getAllEmployees() :List<AppUser>
     @Query("SELECT * FROM app_user WHERE id = :id LIMIT 1")
     suspend  fun getById(id: UUID): AppUser
    @Query("SELECT * FROM app_user WHERE name LIKE '%' || :searchTerm || '%' OR  surname LIKE '%' || :searchTerm || '%'")
    suspend fun  getFilteredEmployees(searchTerm : String): List<AppUser>
    @Insert
    suspend fun  createProfile(entity: Profile)
    @Query("DELETE FROM user_profile")
    suspend fun deleteProfiles()
    @Query("SELECT * FROM user_profile WHERE id =:userId LIMIT 1")
    suspend fun getProfileById(userId: UUID): Profile?



}