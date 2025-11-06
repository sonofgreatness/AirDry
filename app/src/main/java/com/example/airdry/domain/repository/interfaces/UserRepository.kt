package com.example.airdry.domain.repository.interfaces

import androidx.room.Insert
import androidx.room.Query
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Organization
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.database.Entities.UserManagement.UserType
import java.util.UUID

interface UserRepository {

    suspend fun createEmployee(entity: AppUser)
    suspend fun editEmployee(entity: AppUser)
    suspend fun getAllEmployees() :List<AppUser>
    suspend  fun getById(id: UUID): AppUser?
    suspend fun  getFilteredEmployees(searchTerm : String) : List<AppUser>?
    suspend fun  createProfile(entity: Profile)
    suspend fun deleteProfiles()
    suspend fun getProfileById(userId: UUID): Profile?
    suspend fun  deleteAllUsers()
    suspend fun createUserType(entity: UserType)
    suspend fun deleteAllUserTypes()
    suspend fun  getAllNestedEmployees():List<NestedAppUser>
    suspend fun  deleteEmployee(entity: AppUser)
    suspend fun  getAllCachedEmployees():List<AppUser>?
    //====ORGANIZATION MANAGEMENT====
    suspend fun  createOrganization(entity: Organization)
    suspend fun  deleteAllOrganizations()
    suspend fun  createDepartment(entity: Department)
    suspend fun  deleteAllDepartments()
    suspend fun getAllDepartments(): List<Department>?
    suspend  fun createSection(entity: Section)
    suspend fun  deleteAllSections()
    suspend fun getAllSections(): List<Section>?
    suspend fun getDepartmentSections(departmentId: UUID): List<Section>?
}