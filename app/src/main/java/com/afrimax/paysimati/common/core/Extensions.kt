package com.afrimax.paysimati.common.core

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.afrimax.paysimati.common.domain.enums.FileType

fun Context.fileTypeFromUri(uri: Uri): FileType? {
    val mime = MimeTypeMap.getSingleton()
    val extension = mime.getExtensionFromMimeType(contentResolver.getType(uri))
    when (extension) {
        "jpg", "jpeg", "png" -> return FileType.IMAGE
        "pdf" -> return FileType.PDF
    }
    return null
}