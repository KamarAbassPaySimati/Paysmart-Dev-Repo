package com.afrimax.paysimati.common.data.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DownloadManager(private val context: Context) {

    suspend fun saveFile(
        responseBody: ResponseBody, fileName: String, mimeType: String, extension: String
    ): GenericResult<String, Errors.Storage> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // API level 29 and above
            saveFileUsingMediaStore(
                responseBody = responseBody,
                fileName = fileName,
                mimeType = mimeType,
                extension = extension
            )
        } else {
            // API levels below 29
            saveFileToLegacyStorage(
                responseBody = responseBody,
                fileName = fileName,
                mimeType = mimeType,
                extension = extension
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun saveFileUsingMediaStore(
        responseBody: ResponseBody, fileName: String, mimeType: String, extension: String
    ): GenericResult<String, Errors.Storage> {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "$fileName.$extension")
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues
        )

        if (uri != null) {
            try {
                val result = withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        responseBody.byteStream().use { inputStream ->
                            copyStream(
                                inputStream = inputStream,
                                outputStream = outputStream,
                                totalBytes = responseBody.contentLength(),
                                fileUri = uri,
                                mimeType = mimeType
                            )
                        }
                    }
                }

                return if (result != null) GenericResult.Success(uri.path ?: "")
                else GenericResult.Error(Errors.Storage.FILE_NOT_FOUND)
            } catch (e: IOException) {
                return GenericResult.Error(Errors.Storage.IO_ERROR)
            } catch (e: NullPointerException) {
                return GenericResult.Error(Errors.Storage.FILE_NOT_FOUND)
            }
        } else {
            return GenericResult.Error(Errors.Storage.FILE_NOT_FOUND)
        }
    }

    private suspend fun saveFileToLegacyStorage(
        responseBody: ResponseBody, fileName: String, mimeType: String, extension: String
    ): GenericResult<String, Errors.Storage> {
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$fileName.$extension"
        )

        val fileUri = FileProvider.getUriForFile(
            context, "${BuildConfig.APPLICATION_ID}.fileprovider", outputFile
        )

        try {
            withContext(Dispatchers.IO) {
                FileOutputStream(outputFile).use { outputStream ->
                    responseBody.byteStream().use { inputStream ->
                        copyStream(
                            inputStream = inputStream,
                            outputStream = outputStream,
                            totalBytes = responseBody.contentLength(),
                            fileUri = fileUri,
                            mimeType = mimeType
                        )
                    }
                }
            }

            return GenericResult.Success(outputFile.absolutePath)
        } catch (e: IOException) {
            return GenericResult.Error(Errors.Storage.IO_ERROR)
        } catch (e: NullPointerException) {
            return GenericResult.Error(Errors.Storage.FILE_NOT_FOUND)
        } catch (e: FileNotFoundException) {
            return GenericResult.Error(Errors.Storage.FILE_NOT_FOUND)
        } catch (e: SecurityException) {
            return GenericResult.Error(Errors.Storage.SECURITY_EXCEPTION)
        }
    }

    private fun copyStream(
        inputStream: InputStream,
        outputStream: OutputStream,
        totalBytes: Long,
        fileUri: Uri,
        mimeType: String
    ) {
        val buffer = ByteArray(1024)
        var bytesRead: Int

        //Show notification

        var downloadedBytes: Long = 0
        val notificationId = System.currentTimeMillis().toInt() + 111

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)

            downloadedBytes += bytesRead
            val progress = (100 * downloadedBytes / totalBytes).toInt()

            showProgressNotification(notificationId, progress)
        }

        inputStream.close()
        outputStream.close()

        completeNotification(
            notificationId = notificationId, fileUri = fileUri, mimeType = mimeType
        )
    }

    private fun showProgressNotification(notificationId: Int, progress: Int) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle("Downloading File")
            .setContentText("Download in progress").setProgress(100, progress, false)
            .setOngoing(true).setAutoCancel(false)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun completeNotification(notificationId: Int, fileUri: Uri, mimeType: String) {
        val openDownloadsIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(fileUri, mimeType)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openDownloadsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Download Complete").setContentText("Your file has been downloaded")
            .setAutoCancel(true).setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        const val CHANNEL_ID = "${BuildConfig.APPLICATION_ID}_channel_id"
    }

}