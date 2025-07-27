package com.example.airdry

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.airdry.ui.nav.Navigation
import com.example.airdry.ui.theme.AirDryTheme
import com.example.airdry.ui.viewmodels.EmployeeViewModel
import com.example.airdry.ui.viewmodels.WorkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mEmployeeViewModel: EmployeeViewModel by viewModels()
    private val mWorkViewModel: WorkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestStoragePermissionIfNeeded()
        enableEdgeToEdge()
        setContent {
            AirDryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(mEmployeeViewModel, mWorkViewModel)
                }
            }
        }
    }


    fun requestStoragePermissionIfNeeded() {
        if (Build.VERSION.SDK_INT in 23..32) {
            val permissions = arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
               android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 100)
        }
    }
}

