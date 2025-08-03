package com.example.airdry.di

import androidx.work.WorkerFactory
import com.example.airdry.domain.workers.factories.MyWorkerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerFactoryModule {

    @Binds
    abstract fun bindWorkerFactory(factory: MyWorkerFactory): WorkerFactory
}