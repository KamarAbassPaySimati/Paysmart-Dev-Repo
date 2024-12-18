package com.afrimax.paysimati.common.data.repository

import android.content.Context
import android.net.Uri
import com.afrimax.paysimati.common.data.utils.CONTENT_TYPE_PDF
import com.afrimax.paysimati.common.domain.enums.FileType
import com.afrimax.paysimati.common.domain.repository.AmplifyRepository
import com.afrimax.paysimati.common.domain.utils.Errors
import com.afrimax.paysimati.common.domain.utils.GenericResult
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.storage.StorageException
import com.amplifyframework.storage.options.StorageUploadInputStreamOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AmplifyRepositoryImpl(
    private val context: Context
) : AmplifyRepository {

    override suspend fun getBearerToken(): GenericResult<String, Errors.Network> {
        return suspendCoroutine { continuation ->
            com.amplifyframework.core.Amplify.Auth.fetchAuthSession({
                it as AWSCognitoAuthSession
                val result = it.userPoolTokensResult.value
                if (result != null) continuation.resume(GenericResult.Success(result.idToken.toString()))
            }, {
                continuation.resume(GenericResult.Error(Errors.Network.UNAUTHORIZED))
            })
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override suspend fun uploadFile(
        fileUri: Uri, objectKey: String, fileType: FileType
    ): GenericResult<String, Errors.Network> {

        val stream = context.contentResolver.openInputStream(fileUri) ?: return GenericResult.Error(
            Errors.Network.FILE_NOT_FOUND
        )

        val contentType: String? = when (fileType) {
            FileType.IMAGE -> null
            FileType.PDF -> CONTENT_TYPE_PDF
        }

        val options = StorageUploadInputStreamOptions.builder().apply {
            contentType?.let { contentType(it) }
        }.build()

        val upload =
            Amplify.Storage.uploadInputStream(key = objectKey, local = stream, options = options)
        return try {
            val result = upload.result()
            GenericResult.Success(result.key)
        } catch (error: StorageException) {
            GenericResult.Error(Errors.Network.UNABLE_TO_UPLOAD)
        }
    }
}