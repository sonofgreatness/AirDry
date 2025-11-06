package com.example.airdry.util.operational_states

sealed class  DownloadState {
    data class  Success(val message: String) : DownloadState()
    data class  Error(val message: String) : DownloadState()
    object  Loading : DownloadState()
}