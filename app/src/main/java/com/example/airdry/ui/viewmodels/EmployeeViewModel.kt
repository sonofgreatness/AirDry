package com.example.airdry.ui.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.Employee
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel
    @Inject constructor(    private val application: Application,
                            private val repository: EmployeeRepository):
    ViewModel() {



    private val _allEmployees = MutableLiveData<List<Employee>>()
    val allEmployees: LiveData<List<Employee>>
        get() = _allEmployees

    init {
        getAllEmployees()

    }



    private fun getAllEmployees() {
        viewModelScope.launch {
            Toast.makeText(application, "Getting Employees ... ${repository.getAllEmployees().size}", Toast.LENGTH_SHORT).show()
            _allEmployees.value = repository.getAllEmployees()
        }
    }
     fun createEmployee(entity: Employee) {
        viewModelScope.launch {
            Toast.makeText(application, "Employee Created", Toast.LENGTH_SHORT).show()
            repository.createEmployee(entity)
        }
    }
     fun editEmployee(entity: Employee) {
        viewModelScope.launch {
            Toast.makeText(application, "Employee Edited", Toast.LENGTH_SHORT).show()
            repository.editEmployee(entity)

        }
    }

    suspend fun getEmployeeById(id: UUID): Employee? {
        return repository.getById(id)
    }

    }