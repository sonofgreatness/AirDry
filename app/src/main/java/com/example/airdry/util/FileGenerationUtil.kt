package com.example.airdry.util
import android.content.Context
import android.os.Environment
import com.example.airdry.R
import com.example.airdry.database.Entities.WorkWithStack
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class FileGenerationUtil  (
    private val app: Context,
    private val employeeRepo: EmployeeRepository,
    private val workRepo: WorkDoneRepository
) {


    suspend fun  writeExternalWorkBook(startDate: String, endDate: String): File {

        val internalhostPath: String = Environment.getExternalStorageDirectory().path
            .plus("/").plus(app.applicationContext.getString(R.string.app_name))
            .plus("/Excel")
        val workWithStack = workRepo.getWorkWithStackBetweenDates(startDate, endDate)

        if (workWithStack.isEmpty()) {
            throw Exception("No work data found in selected date range.")
        }

        // Step 2: Create Excel workbook
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Work Report")
        val summariesSheet = workbook.createSheet("Daily Summary Report")
        val monthlySummariesSheet = workbook.createSheet("Monthly Summary  Report")

        printHeaders(summariesSheet, sheet, monthlySummariesSheet, workbook)
        printWorkReportData(workWithStack, sheet)
        printDailySummaryData(workWithStack,summariesSheet)
        printMonthlySummaryData(workWithStack,monthlySummariesSheet,startDate, endDate)

        val internalFile = writeToExternal(startDate, endDate, internalhostPath)
        FileOutputStream(internalFile).use { workbook.write(it) }
        workbook.close()
        return internalFile
    }

     suspend fun writeIntenalWorkBook(startDate: String, endDate: String): File {
        val internalhostPath: String = app.filesDir.path
        val workWithStack = workRepo.getWorkWithStackBetweenDates(startDate, endDate)

        if (workWithStack.isEmpty()) {
            throw Exception("No work data found in selected date range.")
        }

        // Step 2: Create Excel workbook
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Work Report")
        val summariesSheet = workbook.createSheet("Daily Summary Report")
        val monthlySummariesSheet = workbook.createSheet("Monthly Summary  Report")


         printHeaders(summariesSheet, sheet, monthlySummariesSheet, workbook)
         printWorkReportData(workWithStack, sheet)
        printDailySummaryData(workWithStack,summariesSheet)
        printMonthlySummaryData(workWithStack, monthlySummariesSheet, startDate, endDate)

        val internalFile = writeToInternal(startDate, endDate, internalhostPath)
        FileOutputStream(internalFile).use { workbook.write(it) }
        workbook.close()
        return internalFile
    }

    private fun printHeaders(
        summariesSheet: XSSFSheet?,
        sheet: XSSFSheet?,
        monthlySummariesSheet: XSSFSheet,
        workbook: XSSFWorkbook,
    ) {
        printDailySummaryReportHeaders(summariesSheet,workbook)
        printWorkReportHeaders(sheet,workbook)
        printMonthlySummaryReportHeaders(monthlySummariesSheet,workbook)
    }

    private fun printMonthlySummaryReportHeaders(sheet: XSSFSheet, workbook: XSSFWorkbook) {
        val headerRow = sheet.createRow(0)
        val headerStyle = createHeaderCellStyle(workbook)

        val headers = listOf("Employee Name", "Date Worked", "total cubes", "total stacks")
        headers.forEachIndexed { index, title ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(title)
            cell.cellStyle = headerStyle
        }
    }

    private fun printDailySummaryReportHeaders(sheet: XSSFSheet?, workbook: XSSFWorkbook) {
        val headerRow = sheet!!.createRow(0)
        val headerStyle = createHeaderCellStyle(workbook)

        val headers = listOf("Employee Name", "Date Worked", "total cubes on day", "total stacks on day")
        headers.forEachIndexed { index, title ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(title)
            cell.cellStyle = headerStyle
        }
    }

    private fun printWorkReportHeaders(sheet: XSSFSheet?, workbook: XSSFWorkbook) {
        val headerRow = sheet!!.createRow(0)
        val headerStyle = createHeaderCellStyle(workbook)

        val headers = listOf(
            "Employee Name",
            "Date Worked",
            "Stack Name",
            "Quantity",
            "% Owned",
            "height",
            "breath",
            "width",
            "numberOfPieces",
            "cubes per stack"
        )

        headers.forEachIndexed { index, title ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(title)
            cell.cellStyle = headerStyle
        }
    }




    fun createHeaderCellStyle(workbook: XSSFWorkbook): XSSFCellStyle {
        val headerStyle = workbook.createCellStyle()

        // Match MaterialTheme.colorScheme.secondaryContainer — choose a close hex value
        // Example: a soft light gray-blue (#E3F2FD) — adjust as needed
        val color = IndexedColors.LIGHT_CORNFLOWER_BLUE.index
        headerStyle.fillForegroundColor = color
        headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

        // Make font bold
        val font: XSSFFont = workbook.createFont()
        font.bold = true
        headerStyle.setFont(font)

        // Add borders (optional, for table-like appearance)
        headerStyle.borderBottom = org.apache.poi.ss.usermodel.BorderStyle.THIN
        headerStyle.borderTop = org.apache.poi.ss.usermodel.BorderStyle.THIN
        headerStyle.borderLeft = org.apache.poi.ss.usermodel.BorderStyle.THIN
        headerStyle.borderRight = org.apache.poi.ss.usermodel.BorderStyle.THIN

        return headerStyle
    }
    /**
     * printMonthlySummaryData : prints the monthly summary of each employee
     *                          where monthly period between startdate - enddate
     *                          summary = total cubes and total stacks done
     *@param workWithStacks : List<WorkWithStack
     * @param sheet:XSSFSheet
     * @param startDate:String
     * @param endDate
     * */
    private suspend  fun printMonthlySummaryData(
        workWithStacks: List<WorkWithStack>,
        sheet: XSSFSheet,
        startDate: String,
        endDate: String
    ) {

        val allActiveEmployees = employeeRepo.getAllEmployees()


        // Group and sort employees by the earliest dateWorked in the list
        val workGroupedByEmployee = workWithStacks.groupBy { it.workDone.employeeId }

        // Build summary records for employees who have work
        val summaries = allActiveEmployees.mapNotNull { employee ->
            val employeeWork = workGroupedByEmployee[employee.id] ?: return@mapNotNull null

            val totalStacks = employeeWork.sumOf { it.workDone.amount * it.workDone.ownershipFraction }
            val totalCubes = employeeWork.sumOf { it.getCubes() }
            val earliestDate = employeeWork.minOf { it.workDone.dateWorked }

            Triple(employee, totalCubes, totalStacks) to earliestDate
        }.sortedBy { it.second } // sort by earliest dateWorked


        var rowIndex = 1
        var grandTotalCubes = 0.0
        var grandTotalStacks = 0.0

        for ((summary, _) in summaries) {
            val (employee, totalCubes, totalStacks) = summary

            val row = sheet.createRow(rowIndex++)
            row.createCell(0).setCellValue(employee.printFullname())
            row.createCell(1).setCellValue("${formatReportDate(startDate)} to  ${formatReportDate(endDate)}")
            row.createCell(2).setCellValue(totalCubes)
            row.createCell(3).setCellValue(totalStacks)

            grandTotalCubes += totalCubes
            grandTotalStacks += totalStacks
        }

        // Add totals row
        val totalRow = sheet.createRow(rowIndex + 1)
        totalRow.createCell(0).setCellValue("TOTAL")
        totalRow.createCell(2).setCellValue(grandTotalCubes)
        totalRow.createCell(3).setCellValue(grandTotalStacks)
    }



    /**
     * printDailySummaryData : prints the daily summary of each employee
     *                        where daily = 24 hour intervals
     *                         summary = total cubes and total stacks done
     *                                   i.e ( find all WorkDoneWithStack per day  for employee
     *                                    and write to sheet)
     *
     * @param workWithStacks : List<WorkWithStack>
     * @param sheet :XSSFSheet
     * */private suspend fun printDailySummaryData(
        workWithStacks: List<WorkWithStack>,
        sheet: XSSFSheet
    ) {


        val allActiveEmployees = employeeRepo.getAllEmployees()

        // Group work by employee ID
        val workGroupedByEmployee = workWithStacks.groupBy { it.workDone.employeeId }

        var rowIndex = 1 // Start after header row

        for (employee in allActiveEmployees) {
            val employeeWork = workGroupedByEmployee[employee.id] ?: continue

            // Group work by DATE (truncated to day only)
            val dailyGrouped = employeeWork.groupBy {
                it.workDone.dateWorked.toLocalDateTime().toLocalDate()
            }

            for ((date, worksOnDate) in dailyGrouped.toSortedMap()) {
                val totalStacks = worksOnDate.sumOf { it.workDone.amount * it.workDone.ownershipFraction }
                val totalCubes = worksOnDate.sumOf { it.getCubes() }
                val row = sheet.createRow(rowIndex++)
                row.createCell(0).setCellValue(employee.printFullname())
                row.createCell(1).setCellValue(formatReportDate(date.toString()))
                row.createCell(2).setCellValue(totalCubes)
                row.createCell(3).setCellValue(totalStacks)
            }
        }
    }

    private suspend fun printWorkReportData(
        workWithStack: List<WorkWithStack>,
        sheet: XSSFSheet
    ) {
        workWithStack.forEachIndexed { index, item ->
            val employee = employeeRepo.getById(item.workDone.employeeId)
            val row = sheet.createRow(index + 1)
            val percentage = item.workDone.ownershipFraction * 100.0
            val roundedPercentage =
                BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP).toDouble()
            
            row.createCell(0).setCellValue(employee?.printFullname() ?: "Unknown")
            row.createCell(1).setCellValue(formatReportDate_(item.workDone.dateWorked.toString()))
            row.createCell(2).setCellValue(item.stack.type)
            row.createCell(3).setCellValue(item.workDone.amount.toDouble())
            row.createCell(4).setCellValue(roundedPercentage)
            row.createCell(5).setCellValue(item.stack.height.toDouble())
            row.createCell(6).setCellValue(item.stack.breadth.toDouble())
            row.createCell(7).setCellValue(item.stack.width.toDouble())
            row.createCell(8).setCellValue(item.stack.numberOfPieces.toDouble())
            row.createCell(9).setCellValue(item.getCubes())
        }
    }





    private fun writeToInternal(
        startDate: String,
        endDate: String,
        internalhostPath: String
    ): File {

        val fileName = "work_report_${formatReportDate(startDate)}_to_${formatReportDate(endDate)}.xlsx"
        val fullPath = File(internalhostPath)
        val internalFile = File(fullPath, fileName)

        return internalFile
    }


    private fun writeToExternal(

        startDate: String,
        endDate: String,
        hostPath: String

    ):File {

        val fileName = "work_report_${formatReportDate(startDate)}_to_${formatReportDate(endDate)}.xlsx"
        val fullPath = File(hostPath)
        val externalFile = File(fullPath, fileName)
        return externalFile
    }
}


fun formatReportDate(dateString: String): String {
    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE // yyyy-MM-dd
    val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH)
    return LocalDate.parse(dateString, inputFormatter).format(outputFormatter)
}

fun formatReportDate_(dateTimeString: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH)
    val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH)
    val dateTime = java.time.LocalDateTime.parse(dateTimeString, inputFormatter)
    return dateTime.toLocalDate().format(outputFormatter)
}