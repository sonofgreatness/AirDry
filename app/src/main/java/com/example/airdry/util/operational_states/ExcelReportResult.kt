package com.example.airdry.util.operational_states

sealed class ExcelReportResult {
    data class Success(val filePath: String) : ExcelReportResult()
    data class Error(val message: String) : ExcelReportResult()
}
