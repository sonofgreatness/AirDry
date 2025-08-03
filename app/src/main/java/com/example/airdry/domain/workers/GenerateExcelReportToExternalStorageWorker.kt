package com.example.airdry.domain.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.airdry.R
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_CHANNEL
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_NOTIFICATION_DESCRIPTION
import com.example.airdry.util.Constants.Companion.GENERATE_FILE_NOTIFICATION_TITLE
import com.example.airdry.util.Constants.Companion.NOTIFICATION_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random


class GenerateExcelReportToExternalStorageWorker @AssistedInject constructor(
    private val employeeRepo: EmployeeRepository,
    private val workRepo: WorkDoneRepository,
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(appContext, params)

{

    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): GenerateExcelReportToExternalStorageWorker
    }

    override suspend fun doWork(): Result {
        startForegroundService()
        writeWorkBook()
        return Result.success()

    }

    private suspend fun writeWorkBook() {
        val hostPath: String = Environment.getExternalStorageDirectory().path
            .plus("/").plus(appContext.getString(R.string.app_name))
            .plus("/Excel")

        val startDate = params.inputData.getString("startDate").toString()
        val endDate = params.inputData.getString("endDate").toString()

        val workWithStack = workRepo.getWorkWithStackBetweenDates(startDate, endDate)

        if (workWithStack.isEmpty()) {
            throw Exception("No work data found in selected date range. [from worker]")
        }

        // Step 2: Create Excel workbook
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Work Report")
        // Step 3: Header

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

        val externalFile = writeToExternal(startDate, endDate, hostPath)
        workbook.write(externalFile.outputStream())
        workbook.close()
    }


    private fun writeToExternal(

        startDate: String,
        endDate: String,
        hostPath: String

    ):File {

        val fileName = "work_report_${startDate}_to_${endDate}.xlsx"
        val fullPath = File(hostPath)
        val externalFile = File(fullPath, fileName)
        return externalFile
    }

    private suspend fun startForegroundService() {
        Log.d("met", "startForegroundService called ")
        setForeground(
            ForegroundInfo(
                NOTIFICATION_ID,
                NotificationCompat.Builder(appContext,
                    GENERATE_FILE_CHANNEL)
                    .setSmallIcon(R.drawable.baseline_file_present_24)
                    .setContentText(GENERATE_FILE_NOTIFICATION_DESCRIPTION)
                    .setContentTitle(GENERATE_FILE_NOTIFICATION_TITLE)
                    .build()
            )
        )
    }
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(appContext, GENERATE_FILE_CHANNEL)
            .setContentTitle(GENERATE_FILE_NOTIFICATION_TITLE)
            .setContentText(GENERATE_FILE_NOTIFICATION_DESCRIPTION)
            .setSmallIcon(R.drawable.baseline_file_present_24)
            .setOngoing(true)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

}