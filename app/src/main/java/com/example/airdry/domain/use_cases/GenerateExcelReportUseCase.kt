package com.example.airdry.domain.use_cases

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
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
) {
    operator fun invoke(
        startDate: String,
        endDate: String
    ): Flow<ExcelReportResult> = flow {
        try {
            val context = app.applicationContext
            Log.d("ExcelReport", "Invoked with startDate=$startDate and endDate=$endDate")

            val workWithStack = workRepo.getWorkWithStackBetweenDates(startDate, endDate)
            Log.d("ExcelReport", "Fetched ${workWithStack.size} work records")

            if (workWithStack.isEmpty()) {
                emit(ExcelReportResult.Error("No work data found in selected date range."))
                return@flow
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

            // Step 4: Fill data
            workWithStack.forEachIndexed { index, item ->
                val employee = employeeRepo.getById(item.workDone.employeeId)
                val row = sheet.createRow(index + 1)
                val percentage = item.workDone.ownershipFraction * 100.0
                val roundedPercentage = BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP).toDouble()

                row.createCell(0).setCellValue(employee?.printFullname() ?: "Unknown")
                row.createCell(1).setCellValue(item.workDone.dateWorked.toString())
                row.createCell(2).setCellValue(item.stack.type.displayName)
                row.createCell(3).setCellValue(item.workDone.amount.toDouble())
                row.createCell(4).setCellValue(roundedPercentage)
            }

            // Step 5: Determine output location
            val fileName = "work_report_${startDate}_to_${endDate}.xlsx"
            val childFolder = FILE_PATH
            val fullPath = File(context.filesDir, childFolder)

            val dirCreated = fullPath.mkdirs() || fullPath.exists()
            val internalFile = File(fullPath, fileName)

            Log.d("ExcelReport", "Created dir=${fullPath.absolutePath}, success=$dirCreated")

            if (!dirCreated) {
                emit(ExcelReportResult.Error("Failed to create directory: ${fullPath.absolutePath}"))
                return@flow
            }



            FileOutputStream(internalFile).use { workbook.write(it) }
            workbook.close()

/*
            // 3. Attempt to copy to external Documents folder (if allowed)
            val externalFilePath = tryCopyToExternalStorage(context, internalFile)

            // 4. Emit success, returning the internal path and (optionally) the shared one
            val message = if (externalFilePath != null) {
                "Report saved to internal and shared storage: $externalFilePath"
            } else {
                "Report saved to internal app folder: ${internalFile.absolutePath}"
            }
*/


            Log.d("ExcelReport", "Excel.. saved to: ${internalFile.absolutePath}")
            emit(ExcelReportResult.Success(internalFile.absolutePath))

        } catch (e: Exception) {
            Log.e("ExcelReport", "Error generating report", e)
            emit(ExcelReportResult.Error("Failed to generate Excel report: ${e.localizedMessage}"))
        }
    }
}
private fun tryCopyToExternalStorage(context: Context, sourceFile: File): String? {
    val TAG = "ExcelExport"

    return try {
        Log.d(TAG, "Attempting to copy file: ${sourceFile.absolutePath}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "Using scoped storage (API >= 29)")

            val targetDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                "AirDry/Reports/Excel"
            )

            Log.d(TAG, "Target directory for scoped storage: ${targetDir.absolutePath}")

            if (!targetDir.exists()) {
                val created = targetDir.mkdirs()
                Log.d(TAG, "Created target directory: $created")
            }

            val destFile = File(targetDir, sourceFile.name)
            Log.d(TAG, "Destination file path: ${destFile.absolutePath}")

            sourceFile.copyTo(destFile, overwrite = true)
            Log.d(TAG, "File copied successfully to scoped storage")

            return destFile.absolutePath
        } else {
            Log.d(TAG, "Using legacy storage (API < 29)")

            val publicDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "AirDry/Reports/Excel"
            )

            Log.d(TAG, "Public documents directory: ${publicDir.absolutePath}")

            if (!publicDir.exists()) {
                val created = publicDir.mkdirs()
                Log.d(TAG, "Created public directory: $created")
            }

            val destFile = File(publicDir, sourceFile.name)
            Log.d(TAG, "Destination file path: ${destFile.absolutePath}")

            sourceFile.copyTo(destFile, overwrite = true)
            Log.d(TAG, "File copied successfully to public storage")

            return destFile.absolutePath
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to copy file: ${e.message}", e)
        return null
    }
}
