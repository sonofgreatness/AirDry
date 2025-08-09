package com.example.airdry.di

import com.example.airdry.BuildConfig
import com.example.airdry.domain.REST_API.interfaces.GithubApi
import com.example.airdry.util.Constants.Companion.GITHUB_API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @GitHubRetrofit
    fun provideGitHubRetrofit(): Retrofit {
        val token = BuildConfig.HUB_PAT

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "token $token")
                    .addHeader("Accept", "application/vnd.github+json")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(GITHUB_API) // "https://api.github.com/"
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGitHubApi(@GitHubRetrofit retrofit: Retrofit): GithubApi =
        retrofit.create(GithubApi::class.java)
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GitHubRetrofit
