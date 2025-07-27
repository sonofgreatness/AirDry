package com.example.airdry.ui.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.airdry.database.Entities.Employee
import com.example.airdry.database.Entities.Stack
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.database.Entities.WorkWithStack
import com.example.airdry.domain.use_cases.GenerateExcelReportUseCase
import com.example.airdry.util.operational_states.ExcelReportResult

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val app: Application,
    private val workDoneRepo: WorkDoneRepository,
    private val stackRepo: StackRepository,
    private val employeeRepo: EmployeeRepository,
    private val generateExcelReportUseCase: GenerateExcelReportUseCase,
) : ViewModel()
{

    private val _selectedEmployee = MutableLiveData<Employee?>()
    val selectedEmployee: LiveData<Employee?> = _selectedEmployee

    private val _workDoneWithStack = MutableLiveData<Map<UUID, List<WorkWithStack>>>()
    val workDoneWithStack: LiveData<Map<UUID, List<WorkWithStack>>> = _workDoneWithStack




    init{
       loadWorkDoneWithStack()
    }

    fun loadWorkDoneWithStack() {
        viewModelScope.launch {
            val grouped = workDoneRepo.getWorkWithStack().groupBy { it.workDone.employeeId }
            _workDoneWithStack.postValue(grouped)
        }
    }
    fun selectEmployee(employee: Employee) {
        _selectedEmployee.value = employee
    }


    fun saveStackAndWork(stack: Stack, workDone: List<WorkDone>) {

        viewModelScope.launch {
            try {
                stackRepo.createStack(stack)
            }catch (ex : Exception)
            {

                Toast.makeText(app.applicationContext,
                    "Failed to Save Stack :  ${ex.message}",Toast.LENGTH_SHORT).show()
            }

            try {
                workDone.forEach {
                    workDoneRepo.createWork(it)
                }
            }
            catch (ex: Exception)
            {
                Toast.makeText(app.applicationContext,
                    "Failed to Save WorkDone :  ${ex.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }




    suspend fun getEmployeeById(id: UUID): Employee? {
        return employeeRepo.getById(id)
    }

    fun generatExcelReport(startDate: LocalDate, endDate: LocalDate) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val start = startDate.format(formatter)
        val end = endDate.format(formatter)

        viewModelScope.launch {
            generateExcelReportUseCase(start, end).collect { result ->
                when (result) {
                    is ExcelReportResult.Success -> {
                        val filePath = result.filePath
                        val file = File(filePath)
                        val context = app.applicationContext

                        Toast.makeText(context, "FILE CREATED: $filePath", Toast.LENGTH_SHORT).show()

                        try {
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider", // Ensure this matches your manifest
                                file
                            )

                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                            }

                            context.startActivity(intent)

                        } catch (e: Exception) {
                            Log.e("ExcelReport", "Failed to open file: ${e.message}", e)
                            Toast.makeText(context, "Could not open file", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is ExcelReportResult.Error -> {
                        Log.e("ExcelReport", "Error: ${result.message}")
                        Toast.makeText(app.applicationContext, "FAILED: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}