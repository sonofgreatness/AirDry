package com.example.airdry.ui.viewmodelProviderFactories

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.airdry.domain.REST_API.interfaces.GithubApi
import javax.inject.Inject

class ConnectionsViewModelProvider@Inject constructor(
    private val context : Application,
    private val sharedPreferences: SharedPreferences,
    private val api: GithubApi
):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return ConnectionsViewModelProvider(context, sharedPreferences,api)
                as T
    }

}