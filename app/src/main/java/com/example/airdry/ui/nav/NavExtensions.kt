package com.example.airdry.ui.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.navigateTopLevel(route: String) {
    this.navigate(route) {
        popUpTo(this@navigateTopLevel.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
