package com.example.airdry.ui.viewmodels

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airdry.BuildConfig
import com.example.airdry.domain.REST_API.data_class.Asset
import com.example.airdry.domain.REST_API.data_class.ReleaseResponse
import com.example.airdry.domain.REST_API.interfaces.GithubApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONArray
import java.io.File
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ConnectionsViewModel @Inject constructor(
    private val context : Application,
    val sharedPreferences: SharedPreferences,
    private val api: GithubApi
) : ViewModel(){



    private val _releases = MutableStateFlow<List<ReleaseResponse>>(emptyList())
    val releases: StateFlow<List<ReleaseResponse>> = _releases


    init{
        fetchAllReleases()
    }



    fun fetchAllReleases() {
        viewModelScope.launch {
            try {
                val response = api.getAllReleases() // <-- you'll make this endpoint return a list
                if (response.isSuccessful) {
                    response.body()?.string()?.let { json ->
                        val jsonArray = JSONArray(json)
                        val releaseList = mutableListOf<ReleaseResponse>()

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val assetsJson = obj.getJSONArray("assets")
                            val assets = mutableListOf<Asset>()
                            for (j in 0 until assetsJson.length()) {
                                val assetObj = assetsJson.getJSONObject(j)
                                assets.add(Asset(assetObj.getString("browser_download_url")))
                            }
                            releaseList.add(
                                ReleaseResponse(
                                    tag_name = obj.getString("tag_name"),
                                    published_at = obj.getString("published_at"),
                                    assets = assets
                                )
                            )
                        }

                        // Sort newest first by date
                        _releases.value = releaseList.sortedByDescending {
                            Instant.parse(it.published_at) // needs java.time
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ManageUpdates", "Error fetching releases", e)
            }
        }
    }

    fun downloadUpdate(url: String) {
      viewModelScope.launch {
          try {
              val outputFile = File(
                  context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                  "app-latest.apk"
              )

              Log.d("ManageUpdates", " starting download url $url")
              downloadFileWithOkHttp(
                  url = url,
                  outputFile = outputFile,
                  token = BuildConfig.HUB_PAT,
                  onProgress = { bytesRead, total ->
                      val progress = (bytesRead * 100 / total).toInt()
                      // Update your notification progress here
                  }
              )
              Log.d("ManageUpdates", " finished download url $url")

          } catch (ex: Exception) {
              Log.d("ManageUpdates", "url $url")
              Log.d("ManageUpdates", "Error downloading update", ex)
          }
      }
    }



    fun downloadFileWithOkHttp(url: String, outputFile: File, token: String,
                                       onProgress: (bytesRead: Long, contentLength: Long) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "token $token")
            .addHeader("Accept", "application/octet-stream")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body ?: throw IOException("Empty response body")
            val contentLength = body.contentLength()

            body.byteStream().use { inputStream ->
                outputFile.outputStream().use { outputStream ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesReadTotal = 0L
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        bytesReadTotal += bytesRead
                        onProgress(bytesReadTotal, contentLength)
                    }
                    outputStream.flush()
                }
            }
        }
    }


}