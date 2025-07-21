package com.example.airdry.ui.viewmodelProviderFactories

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import javax.inject.Inject

class WorkDoneViewModelProvider

@Inject constructor(
                    private val app: Application,
                    private val workDoneRepository: WorkDoneRepository,
                    private val stackRepository: StackRepository,
                    private val employeeRepository: EmployeeRepository
    ) : ViewModelProvider.Factory
{
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return WorkDoneViewModelProvider(app, workDoneRepository, stackRepository, employeeRepository) as T
    }

}