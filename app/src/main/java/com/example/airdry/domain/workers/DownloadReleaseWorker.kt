package com.example.airdry.domain.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.airdry.BuildConfig
import com.example.airdry.R
import com.example.airdry.domain.repository.interfaces.EmployeeRepository
import com.example.airdry.domain.repository.interfaces.WorkDoneRepository
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_CHANNEL_DESCRIPTION
import com.example.airdry.util.Constants.Companion.DOWNLOAD_FILE_NOTIFICATION_TITLE
import com.example.airdry.util.Constants.Companion.DOWNLOAD_NOTIFICATION_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class DownloadReleaseWorker
@AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(appContext, params)
{
    override suspend fun doWork(): Result {
     startForegroundService()
        try {
            val url = params.inputData.getString("url").toString()

            downloadUpdate(url)
        }
        catch (ex: Exception){
            Log.d("Download worker", "${ex.message}")
            return Result.failure()
        }
     return Result.success()
    }

    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): DownloadReleaseWorker
    }


    @Throws(Exception::class)
    fun downloadUpdate(url: String) {

            try {
                val outputFile = File(
                    appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "app-latest.apk"
                )

                Log.d("ManageUpdates", " starting download url $url")
                Log.d("ManageUpdates", " starting download toke ${BuildConfig.HUB_PAT}")
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
                throw ex
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
    private suspend fun startForegroundService() {
        Log.d("met", "startForegroundService called ")
        setForeground(
            ForegroundInfo(
                DOWNLOAD_NOTIFICATION_ID,
                NotificationCompat.Builder(appContext,
                    DOWNLOAD_FILE_CHANNEL)
                    .setSmallIcon(R.drawable.cloud_download)
                    .setContentText(DOWNLOAD_FILE_CHANNEL_DESCRIPTION)
                    .setContentTitle(DOWNLOAD_FILE_NOTIFICATION_TITLE)
                    .build()
            )
        )
    }
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat.Builder(appContext, DOWNLOAD_FILE_CHANNEL)
            .setContentTitle(DOWNLOAD_FILE_NOTIFICATION_TITLE)
            .setContentText(DOWNLOAD_FILE_CHANNEL_DESCRIPTION)
            .setSmallIcon(R.drawable.cloud_download)
            .setOngoing(true)
            .build()

        return ForegroundInfo(DOWNLOAD_NOTIFICATION_ID, notification)
    }
}