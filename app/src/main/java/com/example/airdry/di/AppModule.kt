package com.example.airdry.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.work.WorkerFactory
import com.example.airdry.database.Daos.EmployeeDao
import com.example.airdry.database.Daos.StackDao
import com.example.airdry.database.Daos.WorkDoneDao
import com.example.airdry.database.MainDatabase
import com.example.airdry.domain.REST_API.interfaces.GithubApi
import com.example.airdry.domain.repository.implementations.EmployeeRepositoryImpl
import com.example.airdry.domain.repository.implementations.ReportConfigRepositoryImpl
import com.example.airdry.domain.repository.implementations.StackRepositoryImpl
import com.example.airdry.domain.repository.implementations.WorkDoneRepositoryImpl
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.ReportConfigRepository
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.domain.workers.factories.MyWorkerFactory
import com.example.airdry.util.Constants.Companion.GITHUB_API
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }



    /************************************************
     * LOCAL DATABASE
     ************************************************/
    @Provides
    @Singleton //to ensure single instance
    fun providesEmployeeDao(app: Application): EmployeeDao {
        return MainDatabase.getDatabase(app).getEmployeeDao()
    }

    @Provides
    @Singleton //to ensure single instance
    fun providesStackDao(app: Application): StackDao {
        return MainDatabase.getDatabase(app).getStackDao()
    }

    @Provides
    @Singleton //to ensure single instance
    fun providesWorkDoneDao(app: Application): WorkDoneDao {
        return MainDatabase.getDatabase(app).getWorkDoneDao()
    }
    /****************************************************************/

    @Provides
    @Singleton //to ensure single instance
    fun providesEmployeeRepository(employeeDao: EmployeeDao): EmployeeRepository {
        return EmployeeRepositoryImpl(employeeDao)

    }
    @Provides
    @Singleton //to ensure single instance
    fun providesStackRepository(stackDao: StackDao): StackRepository {
        return StackRepositoryImpl(stackDao)
    }
    @Provides
    @Singleton
    fun providesWorkDoneRepository(workDoneDao: WorkDoneDao): WorkDoneRepository {
        return  WorkDoneRepositoryImpl(workDoneDao)
    }
    @Provides
    @Singleton
    fun providesReportConfigRepository(app: Application): ReportConfigRepository {
        return ReportConfigRepositoryImpl(app)
    }
}