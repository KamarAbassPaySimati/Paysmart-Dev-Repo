package com.afrimax.paysimati.common.core

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.common.domain.enums.FileType
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.internal.http.HTTP_LOCKED
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_CONFLICT
import java.net.HttpURLConnection.HTTP_CREATED
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

fun Context.fileTypeFromUri(uri: Uri): FileType? {
    val mime = MimeTypeMap.getSingleton()
    val extension = mime.getExtensionFromMimeType(contentResolver.getType(uri))
    when (extension) {
        "jpg", "jpeg", "png" -> return FileType.IMAGE
        "pdf" -> return FileType.PDF
    }
    return null
}
fun Any?.log(id: String = "mylog") {

    if (BuildConfig.DEBUG) println("$id: ${Gson().toJson(this)}")
}

fun Any?.parseDate(): Date? {
    return when (this) {
        is String -> try {
            val unixValue = this.toLong()
            val unixTimeMillis = unixValue * 1000
            Date(unixTimeMillis)
        } catch (e: NumberFormatException) {
            e.log()
            null
        }

        is Long -> {
            val unixTimeMillis = this * 1000
            Date(unixTimeMillis)
        }

        is Double, is Float -> {
            val unixValue = this.toString().toDouble().toLong()
            val unixTimeMillis = unixValue * 1000
            Date(unixTimeMillis)
        }

        else -> return null
    }
}
fun Date.parseMalawianDate(format: String): String {
    val cal = Calendar.getInstance().apply { time = this@parseMalawianDate }
    // Define the desired date format
    val formattedDate = SimpleDateFormat(format, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("Africa/Blantyre")
    }.format(cal.time)
    return formattedDate
}

fun Date.clearTimeToMalawiTimeZone(): Date {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Blantyre")).apply {
        time = this@clearTimeToMalawiTimeZone
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}


fun Double.parseCurrency(): String {
    return String.format(Locale.US, "%,.2f", abs(this))
}

