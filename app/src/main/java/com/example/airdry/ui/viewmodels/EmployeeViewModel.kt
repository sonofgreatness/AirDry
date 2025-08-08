package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
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

    private val _filteredEmployees = MutableLiveData<List<Employee>>()
    val filteredEmployees :LiveData<List<Employee>>
        get() = _filteredEmployees


    init {
        loadAllEmployees()
        getAllEmployees()
    }
     fun getAllEmployees() {
        viewModelScope.launch {
          _allEmployees.value = repository.getAllEmployees().toList()
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
            getAllEmployees()

        }
    }


    fun updateFilteredEmployees(searchTerm :String){

    viewModelScope.launch{
        repository.getFilteredEmployees(searchTerm).let{
            _filteredEmployees.postValue(it)
        }
    }
    }

    fun loadAllEmployees() {
        viewModelScope.launch {
            val all = repository.getFilteredEmployees("") ?: emptyList()
            _filteredEmployees.postValue(all)
            Log.d("loadEmployees", " loadEmployees called size -> ${all.size}")
        }
    }
    suspend fun getEmployeeById(id: UUID): Employee? {
        return repository.getById(id)
    }
    }