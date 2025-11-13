package com.example.airdry.domain.repository.implementations

import com.example.airdry.database.Daos.UserManagement.UserManagementDao
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.UserManagement.Department
import com.example.airdry.database.Entities.UserManagement.NestedAppUser
import com.example.airdry.database.Entities.UserManagement.Organization
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.database.Entities.UserManagement.Section
import com.example.airdry.database.Entities.UserManagement.UserType
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.Constants.Companion.CASUAL_NAME
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(val userManagementDao : UserManagementDao): UserRepository {
    override suspend fun createEmployee(entity: AppUser) = userManagementDao.createEmployee(entity)
    override suspend fun editEmployee(entity: AppUser)  = userManagementDao.editEmployee(entity)
    override suspend fun getAllEmployees()= userManagementDao.getAllEmployees()
    override suspend fun getById(id: UUID)  = userManagementDao.getById(id)
    override  suspend fun  getFilteredEmployees(searchTerm : String)
            = userManagementDao.getFilteredEmployees(searchTerm)
    override suspend fun  createProfile(entity: Profile)  = userManagementDao.createProfile(entity)
    override  suspend fun deleteProfiles()= userManagementDao.deleteProfiles()
    override suspend fun getProfileById(userId: UUID)= userManagementDao.getProfileById(userId)
    override suspend fun  deleteAllUsers() = userManagementDao.deleteAllUsers()
    override suspend fun  getAllNestedEmployees() =
        userManagementDao.getAllNestedEmployees()
    override suspend fun deleteEmployee(entity: AppUser) = userManagementDao.deleteEmployee(entity)
    override suspend fun  getAllCachedEmployees()= userManagementDao.getAllCachedEmployees()
    override suspend fun createUserType(entity: UserType) =userManagementDao.createUserType(entity)
    override suspend fun deleteAllUserTypes() = userManagementDao.deleteAllUserTypes()
    override suspend fun replaceAllUserTypesInTransaction(userTypes: List<UserType>) = userManagementDao.replaceAllUserTypesInTransaction(userTypes)
    override suspend fun getCasualUserType(casual:String)= userManagementDao.getCasualUserType()
    override  suspend fun getOganization(): Organization? = userManagementDao.getOganization()
    override suspend fun getCountOfUsersPendingInsert(): Int {
        return userManagementDao.getCountOfUsersPendingInsert()
    }

    //====ORGANIZATION MANAGEMENT====
    override suspend fun  createOrganization(entity: Organization) = userManagementDao.createOrganization(entity)
    override suspend fun  deleteAllOrganizations() =userManagementDao.deleteAllOrganizations()
    override suspend fun  createDepartment(entity: Department) =userManagementDao.createDepartment(entity)
    override suspend fun  deleteAllDepartments() =userManagementDao.deleteAllDepartments()
    override suspend fun getAllDepartments(): List<Department>? = userManagementDao.getAllDepartments()
    override suspend  fun createSection(entity: Section) =userManagementDao.createSection(entity)
    override suspend fun  deleteAllSections() = userManagementDao.deleteAllSections()
    override suspend fun getAllSections() = userManagementDao.getAllSections()
    override suspend fun getDepartmentSections(departmentId: UUID) =userManagementDao.getDepartmentSections(departmentId)
}