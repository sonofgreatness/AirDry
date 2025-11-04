package com.example.airdry.util.operational_states

sealed class LoggedInState {
    object LoggedIn :LoggedInState()
    object LoggedOutNoServer :LoggedInState()
    object Loading :LoggedInState()
    object Idle :LoggedInState()
    object LoggedOutServerActive :LoggedInState()


}