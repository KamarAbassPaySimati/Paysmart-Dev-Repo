package com.afrimax.paysimati.util

import android.util.Base64
import com.afrimax.paysimati.BuildConfig
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCrypt {
    fun encrypt(string: String): String {
        val secretKey = BuildConfig.PASSWORD_SECRET_KEY
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.ENCRYPT_MODE, SecretKeySpec(secretKey.toByteArray(), "AES"), IvParameterSpec(iv)
        )
        val encryptedData = cipher.doFinal(string.toByteArray())

        val encodedIv = Base64.encodeToString(iv, 0)
        val encodedData = Base64.encodeToString(encryptedData, 0)

        return "$encodedIv:$encodedData".replace("\n", "")
    }

}