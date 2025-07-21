package com.example.airdry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        enableEdgeToEdge()
        setContent {
            AirDryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigation(mEmployeeViewModel, mWorkViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AirDryTheme {
        Greeting("Android")
    }
}