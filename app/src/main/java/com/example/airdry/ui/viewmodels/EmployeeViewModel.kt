package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.domain.repository.interfaces.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel
    @Inject constructor(    private val application: Application,
                            private val repository: UserRepository):
    ViewModel() {
    private val _allEmployees = MutableLiveData<List<AppUser>>()
    val allEmployees: LiveData<List<AppUser>>
        get() = _allEmployees

    private val _filteredEmployees = MutableLiveData<List<AppUser>>()
    val filteredEmployees :LiveData<List<AppUser>>
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
     fun createEmployee(entity: AppUser) {
        viewModelScope.launch {
            Toast.makeText(application, "Employee Created", Toast.LENGTH_SHORT).show()
            repository.createEmployee(entity)
        }
    }
     fun editEmployee(entity: AppUser) {
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
    suspend fun getEmployeeById(id: UUID): AppUser? {
        return repository.getById(id)
    }
    }