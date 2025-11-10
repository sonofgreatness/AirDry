package com.example.airdry.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.ConnectivityObserver
import com.example.airdry.auth.SessionManager
import com.example.airdry.database.Entities.UserManagement.Profile
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.util.operational_states.LoggedInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val app: Application,
    private val sessionManager: SessionManager,
    private val connectivityObserver: ConnectivityObserver,
    private val userRepository: UserRepository
) : ViewModel() {

    // --- State: Data for the UI ---
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> get() = _profile
    private  val _loggedInState = MutableStateFlow<LoggedInState>(LoggedInState.Idle)

    // --- State: Derived state from dependencies (network/login status) ---
    val loggedInState: StateFlow<LoggedInState> = combine(
        sessionManager.isLoggedInFlow,
        connectivityObserver.isServerReachable
    ) { isLoggedIn, isNetwork ->
        when {
            isLoggedIn && isNetwork -> LoggedInState.LoggedIn
            isLoggedIn && !isNetwork -> LoggedInState.LoggedIn
            !isLoggedIn && isNetwork -> LoggedInState.LoggedOutServerActive
            !isLoggedIn && !isNetwork -> LoggedInState.LoggedOutNoServer
            else -> LoggedInState.Idle
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LoggedInState.Idle
    )
 private val _uiEvent = Channel<ProfileUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    fun loadProfile(userId: UUID) {
        Log.d("ProfileViewModel", "loadProfile called logged in State : ${loggedInState.value}isLoggeIn ${sessionManager.isLoggedInFlow.value} network ${connectivityObserver.isServerReachable.value}")
        viewModelScope.launch {
            _uiEvent.send(ProfileUiEvent.ShowToast("Loading profile..."))
            _profile.value = null
            _loggedInState.value = LoggedInState.Loading

            try {
                val fetchedProfile = userRepository.getProfileById(userId)
                _profile.value = fetchedProfile
                _loggedInState.value = LoggedInState.LoggedIn
            } catch (e: Exception) {
                _loggedInState.value = LoggedInState.Idle
                _uiEvent.send(ProfileUiEvent.ShowToast("Error loading profile: ${e.message}"))
            }
        }
    }
 fun logout() {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Logout called0")
            sessionManager.logout()
            Log.d("ProfileViewModel", "Logout called1")
            userRepository.deleteProfiles()
            _profile.value = null
        }
    }
}

// --- Companion Class for UI Events (for Toasts) ---
sealed class ProfileUiEvent {
    data class ShowToast(val message: String) : ProfileUiEvent()
    // Add other events like Navigate, ShowSnackbar, etc. here
}