package com.example.airdry.ui.viewmodels

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.airdry.database.Entities.UserManagement.AppUser
import com.example.airdry.database.Entities.Stack.Stack
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import androidx.lifecycle.viewModelScope
import com.example.airdry.database.Entities.WorkDone
import com.example.airdry.database.Entities.WorkWithStack
import com.example.airdry.domain.use_cases.GenerateExcelReportUseCase
import com.example.airdry.util.classes.ExcelFileItem
import com.example.airdry.util.operational_states.ExcelReportResult
import com.example.airdry.util.operational_states.ExcelReportUiState

import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val employeeRepo: UserRepository,
    private val generateExcelReportUseCase: GenerateExcelReportUseCase,
) : ViewModel()
{

    private val _selectedAppUser = MutableLiveData<AppUser?>()
    val selectedAppUser: LiveData<AppUser?> = _selectedAppUser

    private val _allStacks = MutableLiveData<List<Stack?>>()
    val allStacks : LiveData<List<Stack?>>  get() = _allStacks

    private val _uiState = MutableLiveData(ExcelReportUiState())
    val uiState: LiveData<ExcelReportUiState> get() = _uiState

    private val _workDoneWithStack = MutableLiveData<Map<UUID, List<WorkWithStack>>>()
    val workDoneWithStack: LiveData<Map<UUID, List<WorkWithStack>>> = _workDoneWithStack



    private val _files = MutableLiveData<List<ExcelFileItem>>()
    val files: LiveData<List<ExcelFileItem>> get() = _files

    private val filesDirPath: String = app.filesDir.path



    init{
       loadWorkDoneWithStack()
        loadAllStacks()
        loadExcelFiles()
    }

     fun loadAllStacks() {
        viewModelScope.launch{
            /*stackRepo.getAllStacks().let{
                _allStacks.postValue(it)
            }*/
        }
    }

    fun loadWorkDoneWithStack() {
        viewModelScope.launch {
            val grouped = workDoneRepo.getWorkWithStack().groupBy { it.workDone.employeeId }
            _workDoneWithStack.postValue(grouped)
        }
    }
    fun selectEmployee(appUser: AppUser) {
        _selectedAppUser.value = appUser
    }


    fun saveStackAndWork(workDone: List<WorkDone>) {

        viewModelScope.launch {


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





    suspend fun getEmployeeById(id: UUID): AppUser? {
        return employeeRepo.getById(id)
    }





    fun createStack(entity:Stack){

        viewModelScope.launch {
            stackRepo.createStack(entity)
            Toast.makeText(app.applicationContext
            ,"New stack saved", Toast.LENGTH_SHORT).show()
        }
    }

    fun editStack(entity:Stack){

        viewModelScope.launch {
          //  stackRepo.editStack(entity)
            Toast.makeText(app.applicationContext
                ,"Stack edited", Toast.LENGTH_SHORT).show()
        }

    }

    suspend fun  getStackById(id :UUID) : Stack?{

        return null
    //
    //    stackRepo.getStackById(id)
    }



    fun deleteStack(stack: Stack){
        viewModelScope.launch {
            try{
              //  stackRepo.deleteStack(stack)
                Toast.makeText(app.applicationContext, "Stack deleted"
                    ,Toast.LENGTH_SHORT).show()
            }
            catch (ex: Exception){
                Toast.makeText(app.applicationContext, "Failed to delete stack ,:${ex.message} "
                ,Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun generatExcelReport(startDate: LocalDate, endDate: LocalDate) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val start = startDate.format(formatter)
        val end = endDate.format(formatter)

        viewModelScope.launch {
            _uiState.postValue(ExcelReportUiState(isLoading = true))
            generateExcelReportUseCase(start, end).collect { result ->
                when (result) {
                    is ExcelReportResult.Success -> {


                        openGeneratedFile(result.filePath)
                    }
                    is ExcelReportResult.Error -> {

                        _uiState.postValue(ExcelReportUiState(
                            isLoading = false,
                            errorMessage = result.message ?: "Unknown error"
                        ))
                        Toast.makeText(app, "FAILED: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun openGeneratedFile(filePath: String) {
        val file = File(filePath)
        val context = app.applicationContext
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
            _uiState.postValue( ExcelReportUiState(
                isLoading = false,
                successMessage = "Report generated successfully!"
            ))

        } catch (e: Exception) {
            _uiState.postValue(ExcelReportUiState(
                isLoading = false,
                errorMessage = "Could not open file"
            ))
            Log.e("ExcelReport", "Failed to open file: ${e.message}", e)
            Toast.makeText(context, "Could not open file", Toast.LENGTH_SHORT).show()
        }
    }



    fun loadExcelFiles() {
        val dir = File(filesDirPath)
        val fileList = dir.listFiles()
            ?.filter { it.extension.lowercase() == "xlsx" }
            ?.map { ExcelFileItem(it.name, it) }
            ?: emptyList()

        _files.value = fileList
    }

    fun deleteFile(item: ExcelFileItem) {
        if (item.file.exists()) {
            item.file.delete()
            loadExcelFiles()
        }
    }

    fun shareFile(context: Context, item: ExcelFileItem) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            item.file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Excel File"))
    }

    fun openFile(context: Context, item: ExcelFileItem) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            item.file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No app found to open Excel files", Toast.LENGTH_SHORT).show()
        }
    }

}
