package com.example.airdry.domain.REST_API.interfaces

import com.example.airdry.util.Constants.Companion.GITHUB_REPO_URL
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface GithubApi {

    @GET(GITHUB_REPO_URL)
    suspend fun getAllReleases(): Response<ResponseBody>
}