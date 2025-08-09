package com.example.airdry

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.airdry.ui.nav.Navigation
import com.example.airdry.ui.theme.AirDryTheme
import com.example.airdry.ui.viewmodels.ConnectionsViewModel
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.viewmodels.WorkViewModel
import com.example.airdry.util.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mEmployeeViewModel: EmployeeViewModel by viewModels()
    private val mWorkViewModel: WorkViewModel by viewModels()
    private val mConnectionsViewModel : ConnectionsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getFileManagementPermissions()

        setContent {
            AirDryTheme {
                Navigation(mEmployeeViewModel, mWorkViewModel, mConnectionsViewModel)
            }
        }
    }

    private fun getFileManagementPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                showManageFilesPermissionDialog()
            }
        } else {
            requestReadAndWritePermissions()
        }
    }

    private fun requestReadAndWritePermissions() {
        val writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            if (
                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ) {
                Toast.makeText(
                    this,
                    "This app needs file access permissions to generate and store reports.",
                    Toast.LENGTH_LONG
                ).show()
            }

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                   android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                Constants.REQUEST_FILE_PERMISSION
            )
        }
    }

    private fun showManageFilesPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Storage Access Permission")
            .setMessage("Please allow 'All Files Access' so we can save your reports to external storage.")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Grant") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
            .show()
    }
}