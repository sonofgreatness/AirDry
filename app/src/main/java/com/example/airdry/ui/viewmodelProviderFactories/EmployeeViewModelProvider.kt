package com.example.airdry.ui.viewmodelProviderFactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import javax.inject.Inject

class EmployeeViewModelProvider
@Inject constructor(private val application: Application,
                    private val repository: EmployeeRepository)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return EmployeeViewModelProvider(application,repository)
                as T
    }

    }