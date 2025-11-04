package com.example.airdry.util.operational_states

sealed class UiState  {
    object Idle:UiState()
    object Loading:UiState()
    object Success:UiState()
    data class Error(val message: String):UiState()
}