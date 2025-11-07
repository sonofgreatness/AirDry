package com.example.airdry.util.operational_states

sealed class SyncStatus {
    data object Idle : SyncStatus()
    data object Complete : SyncStatus()
    data class Working(val message: String) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}