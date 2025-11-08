package com.example.airdry.database.Daos.UserManagement

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Organization
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.database.Entities.UserManagement.UserType
import com.example.airdry.util.Constants.Companion.CASUAL_NAME
import java.util.UUID

@Dao
interface UserManagementDao {
    @Insert
    suspend fun createEmployee(entity: AppUser)
    @Update
    suspend fun editEmployee(entity: AppUser)
    @Query("""
        SELECT * FROM  app_user WHERE is_active =1
    """)
    suspend fun getAllEmployees() :List<AppUser>

    @Transaction
    @Query("SELECT * FROM app_user")
    suspend fun  getAllNestedEmployees():List<NestedAppUser>
    @Delete
    suspend fun  deleteEmployee(entity: AppUser)
    @Query("SELECT * FROM app_user WHERE is_pending_insert =1")
    suspend fun  getAllCachedEmployees():List<AppUser>?

    @Query("SELECT * FROM app_user WHERE id = :id LIMIT 1")
    suspend  fun getById(id: UUID): AppUser
    @Query("SELECT * FROM app_user WHERE name LIKE '%' || :searchTerm || '%' OR  surname LIKE '%' || :searchTerm || '%'")
    suspend fun  getFilteredEmployees(searchTerm : String): List<AppUser>
    @Query("DELETE FROM app_user")
    suspend fun  deleteAllUsers()
    @Insert
    suspend fun createUserType(entity: UserType)
    @Query ("DELETE FROM user_type")
    suspend fun deleteAllUserTypes()


    @Transaction // <-- THIS IS THE KEY ANNOTATION
    suspend fun replaceAllUserTypesInTransaction(userTypes: List<UserType>) {
       try {
           Log.d("deleteUserTypes", "Deleting all user types")
           deleteAllUserTypes()
           Log.d("deleteUserTypes", "Deleted all user types")
           userTypes.forEach { createUserType(it) } // Inserts new records
       }catch (e : Exception){
           Log.d("deleteUserTypes", "Failed to delete all user types ${e.message}")
       }
    }

    @Query("SELECT * FROM user_type WHERE displayname LIKE '%' || :casual ||'%'")
    suspend fun getCasualUserType(casual:String  = CASUAL_NAME):List<UserType>?
    //====ORGANIZATION MANAGEMENT====
    @Insert
    suspend fun  createOrganization(entity: Organization)
   @Query("DELETE FROM organization")
   suspend fun  deleteAllOrganizations()
    @Query("SELECT * FROM organization LIMIT 1")
    suspend fun getOganization(): Organization?
    @Insert
    suspend fun  createDepartment(entity: Department)
    @Query("DELETE FROM department")
    suspend fun  deleteAllDepartments()
    @Query("SELECT * FROM department")
    suspend fun getAllDepartments(): List<Department>?
    @Insert
    suspend  fun createSection(entity: Section)
    @Query("DELETE FROM section")
    suspend fun  deleteAllSections()
    @Query("SELECT * FROM section")
    suspend fun getAllSections(): List<Section>?
    @Query("SELECT * FROM section WHERE department_id =:departmentId")
    suspend fun getDepartmentSections(departmentId: UUID): List<Section>?

    //====PROFILE MANAGEMENT====
    @Insert
    suspend fun  createProfile(entity: Profile)
    @Query("DELETE FROM user_profile")
    suspend fun deleteProfiles()
    @Query("SELECT * FROM user_profile WHERE id =:userId LIMIT 1")
    suspend fun getProfileById(userId: UUID): Profile?
}