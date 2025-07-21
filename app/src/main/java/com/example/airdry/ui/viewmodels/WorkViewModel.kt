package com.example.airdry.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.airdry.database.Entities.Employee
import com.example.airdry.database.Entities.Stack
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.Enums
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.WorkDone

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val app: Application,
    private val workDoneRepo: WorkDoneRepository,
    private val stackRepo: StackRepository,
    private val employeeRepo: EmployeeRepository
) : ViewModel()
{


    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>>  get()  =  _employees

    private val _selectedEmployee = MutableLiveData<Employee?>()
    val selectedEmployee: LiveData<Employee?> = _selectedEmployee

    init {

        viewModelScope.launch {
            _employees.value = employeeRepo.getAllEmployees()
        }
    }

    fun selectEmployee(employee: Employee) {
        _selectedEmployee.value = employee
    }

    fun createStack(length: Enums.StackLength, width: Enums.StackWidth) {
        val stack = Stack(length = length, width = width)
        viewModelScope.launch {
            stackRepo.createStack(stack)
            Toast.makeText(app, "Stack saved", Toast.LENGTH_SHORT).show()
        }
    }
    fun createStack(length: Enums.StackLength, width: Enums.StackWidth, onCreated: (Stack) -> Unit) {
        viewModelScope.launch {
            val stack = Stack(length = length, width = width)

            stackRepo.createStack(stack)
            onCreated(stack)
        }
    }

    fun createWorkDone(data: WorkDone) {
        viewModelScope.launch {
            workDoneRepo.createWork(data)

        }
    }


    fun saveWork(workDone: WorkDone) {
        viewModelScope.launch {
            workDoneRepo.createWork(workDone)
            Toast.makeText(app, "WorkDone saved", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun getEmployeeById(id: UUID): Employee? {
        return employeeRepo.getById(id)
    }

    suspend fun getStackById(id: UUID): Stack? {
        return stackRepo.getStackById(id)
    }

}