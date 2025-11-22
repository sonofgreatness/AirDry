package com.example.airdry

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import com.example.airdry.data.dto.Common.SimpleResponse
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.util.Constants.Companion.CONNECTIVITY_DELAY_TIME
import com.example.airdry.util.Constants.Companion.SERVER_OK_TEXT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.Logger

class MyConnectivityObserver(
    private  val userApi: UserApi,
    private val logger: Logger,
    private val connectivityObserver: ConnectivityObserver,
    private val application: Application
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val _isServerReachable = MutableStateFlow(false)
    val isServerReachable: StateFlow<Boolean> = _isServerReachable

    init {
        // Observe both device connectivity + server reachability
        scope.launch { observeConnectivity() }
    }

    private suspend fun observeConnectivity() {
        connectivityObserver.observe().collectLatest { status ->
            when (status) {
                ConnectivityObserver.Status.Available -> {
                    Log.d("connectivity", "📡 Network available — checking server health...")
                    startServerHealthCheck()
                }

                ConnectivityObserver.Status.Losing,
                ConnectivityObserver.Status.Lost,
                ConnectivityObserver.Status.Unavailable -> {
                    Log.d("connectivity", "⚠️ Network lost or unavailable")
                    _isServerReachable.value = false
                }
            }
        }
    }

    private suspend fun startServerHealthCheck() {
        val networkObserver = connectivityObserver as? NetworkConnectivityObserver

        while (networkObserver?.isNetworkAvailable() == true) {
            try {

                val response: SimpleResponse = userApi.getServerHealth()
                val success = response.message == SERVER_OK_TEXT

                if (success) {
                    if (!_isServerReachable.value)
                        Log.d("conn", "server reached")

                    _isServerReachable.value = true
                } else {
                    Log.d("conn", "server NOT reached ${response.message}")
                    _isServerReachable.value = false
                }

            } catch (e: Exception) {
                if (_isServerReachable.value) {
                    Log.d("conn", "server NOT reached Exception")
                }
                Log.d("conn", "server NOT reached Exception")
                _isServerReachable.value = false
            }
            delay(CONNECTIVITY_DELAY_TIME) // Check every 60 seconds (adjust as needed)
        }
    }

}