package com.example.airdry.util.operational_states

data class ExcelReportUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)