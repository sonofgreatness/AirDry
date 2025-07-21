package com.example.airdry.domain.repository.implementations

import com.example.airdry.database.Daos.EmployeeDao
import com.example.airdry.database.Entities.Employee
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import java.util.UUID
import javax.inject.Inject

class EmployeeRepositoryImpl @Inject constructor(val employeeDao : EmployeeDao): EmployeeRepository {
    override suspend fun createEmployee(entity: Employee) = employeeDao.createEmployee(entity)
    override suspend fun editEmployee(entity: Employee)  = employeeDao.editEmployee(entity)
    override suspend fun getAllEmployees()= employeeDao.getAllEmployees()
    override suspend fun getById(id: UUID)  = employeeDao.getById(id)
}