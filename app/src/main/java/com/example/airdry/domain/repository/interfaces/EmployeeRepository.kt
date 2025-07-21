package com.example.airdry.domain.repository.interfaces

import com.example.airdry.database.Entities.Employee
import java.util.UUID

interface EmployeeRepository {

    suspend fun createEmployee(entity: Employee)
    suspend fun editEmployee(entity: Employee)
    suspend fun getAllEmployees() :List<Employee>
    suspend  fun getById(id: UUID): Employee?

}