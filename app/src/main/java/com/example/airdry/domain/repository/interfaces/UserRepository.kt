package com.example.airdry.domain.repository.interfaces

import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Profile
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

}