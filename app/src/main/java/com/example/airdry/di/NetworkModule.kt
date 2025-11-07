package com.example.airdry.di

import com.example.airdry.BuildConfig
import com.example.airdry.auth.SessionExpiryRepository
import com.example.airdry.auth.SessionManager
import com.example.airdry.domain.REST_API.implementation.OrganizationApiImpl
import com.example.airdry.domain.REST_API.implementation.StackApiImpl
import com.example.airdry.domain.REST_API.implementation.UserApiImpl
import com.example.airdry.domain.REST_API.interfaces.GithubApi
import com.example.airdry.domain.REST_API.interfaces.OrganizationApi
import com.example.airdry.domain.REST_API.interfaces.StackApi
import com.example.airdry.domain.REST_API.interfaces.UserApi
import com.example.airdry.util.Constants.Companion.GITHUB_API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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

    @Singleton
    @Provides
    fun provideHttpClient(sessionManager: SessionManager): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(DefaultRequest) {
                // Attach JWT header if available
                sessionManager.jwtToken?.let { token ->
                    headers.append(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 30_000 // 30 seconds
                requestTimeoutMillis = 60_000 // 60 seconds
                socketTimeoutMillis = 60_000  // 60 seconds
            }

            engine {
                requestTimeout = 60_000 // For CIO engine-level timeout
                endpoint {
                    connectTimeout = 30_000
                    connectAttempts = 3
                }
            }
        }
    }
    @Provides
    @Singleton
   fun provideUserApi(client: HttpClient, sessionManager: SessionManager,
                      logger: org.slf4j.Logger, sessionExpiryRepository: SessionExpiryRepository): UserApi{
       return UserApiImpl(
           client, sessionManager, logger,
           sessionExpiryRepository
       )
   }
  @Provides
  @Singleton
  fun provideOrganizationApi(client: HttpClient, logger: org.slf4j.Logger) : OrganizationApi{
      return OrganizationApiImpl(client,logger)
  }
  @Provides
  @Singleton
  fun provideStackApi(client: HttpClient, logger: org.slf4j.Logger) : StackApi{
      return StackApiImpl(client = client,logger = logger)
  }
}
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GitHubRetrofit
