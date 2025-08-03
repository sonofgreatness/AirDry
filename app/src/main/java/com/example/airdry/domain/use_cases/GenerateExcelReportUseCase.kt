package com.example.airdry.domain.use_cases

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.airdry.R
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.ReportConfigRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.domain.workers.GenerateExcelReportToExternalStorageWorker
import com.example.airdry.util.Constants.Companion.FILE_PATH
import com.example.airdry.util.operational_states.ExcelReportResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class GenerateExcelReportUseCase @Inject constructor(
    private val app: Application,
    private val employeeRepo: EmployeeRepository,
    private val workRepo: WorkDoneRepository,
    private  val reportConfigRepo: ReportConfigRepository
) {
    operator fun invoke(
        startDate: String,
        endDate: String
    ): Flow<ExcelReportResult> = flow {
        try {



            val internalFile = writeWorkBook(startDate, endDate)
            try {
             val workRequest = OneTimeWorkRequestBuilder<GenerateExcelReportToExternalStorageWorker>()
                              .addTag("generate_excel_report")
                            .setInputData(workDataOf(
                                "startDate" to startDate,
                                 "endDate" to endDate))
                              .build()
                WorkManager.getInstance(app.applicationContext)
                    .beginWith(workRequest)
                    .enqueue()



                
            }catch (ex : Exception)
            {
                Log.d("GenerateExcelReportUseCase","failed to  write  to external  storage ${ex.localizedMessage}")
            }
            emit(ExcelReportResult.Success(internalFile.absolutePath))

        } catch (e: Exception) {
            emit(ExcelReportResult.Error("Failed to generate Excel report: ${e.localizedMessage}"))
        }
    }

    private suspend fun writeWorkBook(startDate: String, endDate: String): File {
        val internalhostPath: String = app.filesDir.path
        val workWithStack = workRepo.getWorkWithStackBetweenDates(startDate, endDate)

        if (workWithStack.isEmpty()) {
            throw Exception("No work data found in selected date range.")
        }

        // Step 2: Create Excel workbook
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Work Report")

        // Step 3: Header
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Employee Name")
        headerRow.createCell(1).setCellValue("Date Worked")
        headerRow.createCell(2).setCellValue("Stack Name")
        headerRow.createCell(3).setCellValue("Quantity")
        headerRow.createCell(4).setCellValue("% Owned")
        headerRow.createCell(5).setCellValue("height")
        headerRow.createCell(6).setCellValue("breath")
        headerRow.createCell(7).setCellValue("width")
        headerRow.createCell(8).setCellValue("numberOfPieces")
        headerRow.createCell(9).setCellValue("cubes per stack")


        // Step 4: Fill data
        workWithStack.forEachIndexed { index, item ->
            val employee = employeeRepo.getById(item.workDone.employeeId)
            val row = sheet.createRow(index + 1)
            val percentage = item.workDone.ownershipFraction * 100.0
            val roundedPercentage =
                BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP).toDouble()

            val dimens = item.stack.type.height * item.stack.type.breadth * item.stack.type.width
            val  sum = item.stack.type.numberOfPieces * item.workDone.amount * item.workDone.ownershipFraction
            val cubes = dimens * sum



            row.createCell(0).setCellValue(employee?.printFullname() ?: "Unknown")
            row.createCell(1).setCellValue(item.workDone.dateWorked.toString())
            row.createCell(2).setCellValue(item.stack.type.displayName)
            row.createCell(3).setCellValue(item.workDone.amount.toDouble())
            row.createCell(4).setCellValue(roundedPercentage)
            row.createCell(5).setCellValue(item.stack.type.height.toDouble())
            row.createCell(6).setCellValue(item.stack.type.breadth.toDouble())
            row.createCell(7).setCellValue(item.stack.type.width.toDouble())
            row.createCell(8).setCellValue(item.stack.type.numberOfPieces.toDouble())
            row.createCell(9).setCellValue(cubes)
        }


        setupDirectories()
        val internalFile = writeToInternal(startDate, endDate, internalhostPath)
        FileOutputStream(internalFile).use { workbook.write(it) }
        workbook.close()
        return internalFile
    }


    private fun setupDirectories() {
        reportConfigRepo.setUpInternalDirectories()
        reportConfigRepo.setUpExternalDirectories()
    }
}




private fun writeToInternal(
    startDate: String,
    endDate: String,
    internalhostPath: String
): File {

    val fileName = "work_report_${startDate}_to_${endDate}.xlsx"
    val childFolder = FILE_PATH
    val fullPath = File(internalhostPath, childFolder)
    val internalFile = File(fullPath, fileName)

    return internalFile
}
