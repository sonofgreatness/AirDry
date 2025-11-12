package com.example.airdry.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.airdry.ConnectivityObserver
import com.example.airdry.MyConnectivityObserver
import com.example.airdry.NetworkConnectivityObserver
import com.example.airdry.auth.SessionExpiryRepository
import com.example.airdry.auth.SessionManager
import com.example.airdry.database.Daos.UserManagement.UserManagementDao
import com.example.airdry.database.Daos.ProducKeyDao
import com.example.airdry.database.Daos.StackDao
import com.example.airdry.database.Daos.WorkDoneDao
import com.example.airdry.database.MainDatabase
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.domain.repository.implementations.UserRepositoryImpl
import com.example.airdry.domain.repository.implementations.ProductKeyRepositoryImpl
import com.example.airdry.domain.repository.implementations.ReportConfigRepositoryImpl
import com.example.airdry.domain.repository.implementations.StackRepositoryImpl
import com.example.airdry.domain.repository.implementations.WorkDoneRepositoryImpl
import com.example.airdry.domain.repository.interfaces.UserRepository
import com.example.airdry.domain.repository.interfaces.ProductKeyRepository
import com.example.airdry.domain.repository.interfaces.ReportConfigRepository
import com.example.airdry.domain.repository.interfaces.StackRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.ui.theme.ThemeManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    fun providesEmployeeDao(app: Application): UserManagementDao {
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

    @Provides
    @Singleton //to ensure single instance
    fun providesProductKeyDao(app: Application): ProducKeyDao {
        return MainDatabase.getDatabase(app).getProducKeyDao()
    }


    /****************************************************************/

    @Provides
    @Singleton //to ensure single instance
    fun providesEmployeeRepository(userManagementDao: UserManagementDao): UserRepository {
        return UserRepositoryImpl(userManagementDao)

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

    @Provides
    @Singleton
    fun providesProductKeyRepository(producKeyDao: ProducKeyDao) : ProductKeyRepository{

        return ProductKeyRepositoryImpl(producKeyDao)
    }
    @Provides
    @Singleton
    fun providesSettings(app: Application): Settings {
        val prefs = app.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
        return SharedPreferencesSettings(prefs)
    }
    @Provides
    @Singleton
    fun providesLogger() : Logger{
        return LoggerFactory.getLogger("BrotherHood Mobile")
    }
    @Provides
    @Singleton
    fun providesessionManager(settings: Settings, logger: Logger
    ): SessionManager{
        return SessionManager(settings,logger)
    }
    @Provides
    @Singleton
    fun provideSessionExpiryRepository(settings: Settings) : SessionExpiryRepository{
        return SessionExpiryRepository(settings)
    }

    @Singleton
    @Provides
    fun providesConnectivityObserver(application: Application) : ConnectivityObserver{
       return NetworkConnectivityObserver(application)
    }


    @Provides
    @Singleton
    fun provideMyConnectivityObserver(  userApi: UserApi,
                                        logger: Logger,connectivityObserver: ConnectivityObserver
                                        ,application: Application) : MyConnectivityObserver{
        return MyConnectivityObserver(userApi,logger,connectivityObserver,application)
    }
    @Provides
    @Singleton
    fun providesThemeManager(settings: Settings): ThemeManager{
        return ThemeManager(settings)
    }


}