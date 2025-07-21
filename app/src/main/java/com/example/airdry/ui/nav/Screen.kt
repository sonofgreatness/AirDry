package com.example.airdry.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed
 class  Screen(val route: String, val title: String, val icon: ImageVector) {
    object Workers : Screen("workers", "Workers", Icons.Default.Person)
    object WorkDone : Screen("workdone", "Work Done", Icons.Default.Build)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object CreateWorker : Screen("workers/create", "New Worker", Icons.Default.Add)
    object EditWorker : Screen("workers/edit", "Edit Worker", Icons.Default.Edit)

    object SelectEmployee : Screen("work/selectEmployee", "Select Employee", Icons.Default.Person)
    object CreateStack : Screen("work/createStack", "Create Stack", Icons.Default.Add)
    object RecordWork : Screen("work/recordWork", "Record Work", Icons.Default.Build)

    fun withArgs(vararg args: String): String {
        return if (args.isNotEmpty()) {
            buildString {
                append(route)
                args.forEachIndexed { index, arg ->

                        append("/$arg")
                }
            }
        } else {
            route
        }
    }
}
