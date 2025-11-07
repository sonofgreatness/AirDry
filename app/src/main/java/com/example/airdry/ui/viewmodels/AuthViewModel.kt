package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.ConnectivityObserver
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.domain.use_cases.UserManagement.CreateLogInProfileUseCase
import com.example.airdry.util.Constants.Companion.FAILED_LOGIN
import com.example.airdry.util.operational_states.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.slf4j.Logger
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appContext: Application,
    private  val userApi: UserApi,
    private val connectivityObserver: ConnectivityObserver,
private  val createLogInProfileUseCase: CreateLogInProfileUseCase
    )
    : ViewModel(){
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    val isNetworkAvailable: StateFlow<Boolean> = connectivityObserver.isServerReachable

    // move form fields here
    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun onUsernameChange(newValue: String) {
        username = newValue
    }
    fun onPasswordChange(newValue: String) {
        password = newValue
    }
    fun clearFields() {
        username = ""
        password = ""
    }
    fun resetState() {
        _uiState.value = UiState.Idle
    }

    fun login()
    {
        loginInternal(isNetworkAvailable.value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loginInternal(isNetworkAvailable: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                if (!isNetworkAvailable) {
                    Toast.makeText(appContext, "no network", Toast.LENGTH_SHORT).show()
                    _uiState.value = UiState.Error("login failed: no network")
                } else {
                   val response = userApi.login(username.trim(), password.trim())
                    if(response) {
                        _uiState.value = UiState.Success
                        createProfile()
                    }else{
                        _uiState.value = UiState.Error(FAILED_LOGIN)
                    }
                }
            } catch (e: Exception) {
                val msg = e.message ?: "Unexpected error"
                Log.d("Login", "Error during login: $msg")
                Toast.makeText(appContext,"Error during login: $msg",Toast.LENGTH_SHORT).show()
                _uiState.value = UiState.Error(FAILED_LOGIN)
            }
        }
    }
    fun createProfile(){
        viewModelScope.launch {
            try {
                createLogInProfileUseCase()
            }catch (e: Exception)
            {
                Log.d("createProfile", "failed ${e.message}")
            }
        }
    }

}