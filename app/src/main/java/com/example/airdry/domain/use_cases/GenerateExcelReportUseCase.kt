package com.example.airdry.domain.use_cases

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.repository.interfaces.ReportConfigRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.domain.workers.GenerateExcelReportToExternalStorageWorker
import com.example.airdry.util.FileGenerationUtil
import com.example.airdry.util.operational_states.ExcelReportResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GenerateExcelReportUseCase @Inject constructor(
    private val app: Application,
    private val employeeRepo: UserRepository,
    private val workRepo: WorkDoneRepository,
    private  val reportConfigRepo: ReportConfigRepository
) {
    @SuppressLint("SuspiciousIndentation")
    operator fun invoke(
        startDate: String,
        endDate: String
    ): Flow<ExcelReportResult> = flow {
        try {

            setupDirectories()

              val generator =  FileGenerationUtil(app
              ,employeeRepo, workRepo
                  )

            val internalFile = generator.writeIntenalWorkBook(startDate,endDate)

            try {
                val workRequest =
                    OneTimeWorkRequestBuilder<GenerateExcelReportToExternalStorageWorker>()
                        .addTag("generate_excel_report")
                        .setInputData(
                            workDataOf(
                                "startDate" to startDate,
                                "endDate" to endDate
                            )
                        )
                        .build()
                WorkManager.getInstance(app.applicationContext)
                    .beginWith(workRequest)
                    .enqueue()


            } catch (ex: Exception) {
                Log.d(
                    "GenerateExcelReportUseCase",
                    "failed to  write  to external  storage ${ex.localizedMessage}"
                )
            }
            emit(ExcelReportResult.Success(internalFile.absolutePath))

        } catch (e: Exception) {
            emit(ExcelReportResult.Error("Failed to generate Excel report: ${e.localizedMessage}"))
        }
    }
    private  fun setupDirectories() {
        reportConfigRepo.setUpInternalDirectories()
        reportConfigRepo.setUpExternalDirectories()
    }

}
