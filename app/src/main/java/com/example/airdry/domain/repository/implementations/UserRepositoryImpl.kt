package com.example.airdry.domain.repository.implementations

import com.example.airdry.database.Daos.UserManagement.EmployeeDao
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.domain.repository.interfaces.UserRepository
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(val employeeDao : EmployeeDao): UserRepository {
    override suspend fun createEmployee(entity: AppUser) = employeeDao.createEmployee(entity)
    override suspend fun editEmployee(entity: AppUser)  = employeeDao.editEmployee(entity)
    override suspend fun getAllEmployees()= employeeDao.getAllEmployees()
    override suspend fun getById(id: UUID)  = employeeDao.getById(id)
    override  suspend fun  getFilteredEmployees(searchTerm : String)
            = employeeDao.getFilteredEmployees(searchTerm)

    override suspend fun  createProfile(entity: Profile)  = employeeDao.createProfile(entity)
  override  suspend fun deleteProfiles()= employeeDao.deleteProfiles()
   override suspend fun getProfileById(userId: UUID): Profile?= employeeDao.getProfileById(userId)



}