package com.example.airdry.util.operational_states


sealed class WorkerReleaseResponse
{
    data class Success(val message: String?) : WorkerReleaseResponse()
    data class Error(val message: String?) : WorkerReleaseResponse()
    data class Loading(val message: String?) : WorkerReleaseResponse()
}