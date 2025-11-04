package com.example.airdry.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed
 class  Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Workers : Screen("workers", "Workers", Icons.Default.Person)
    object WorkDone : Screen("workdone", "Work Done", Icons.Default.Build)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object CreateWorker : Screen("workers/create", "New Worker", Icons.Default.Add)
    object EditWorker : Screen("workers/edit", "Edit Worker", Icons.Default.Edit)
    object SelectEmployee : Screen("select_employee", "Select", Icons.Default.Person)
    object CreateStackAndWork : Screen("create_stack", "Create Stack",null)
    object GenerateExcel : Screen("generate_excel", "Generate Excel", null)
    object ViewExcel : Screen("view_excel", "View Excel", null)
    object ManageUpdates:Screen("manage_updates", "Manage Updates",null )
    object ManageStacks:Screen("manage_stacks", "Manage Stacks", null)
    object CreateStack: Screen ("stacks/save", "New Stack", null)
    object EditStack:Screen("stacks/edit", "Edit Stack", null)
    object  About :Screen("about", "About App", null)
    object Login : Screen("login","Login Screen", null)
    object ProductKey: Screen("product key", "Product Key", null )
    object  Profile:Screen("profile","Profile Screen",null)
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
