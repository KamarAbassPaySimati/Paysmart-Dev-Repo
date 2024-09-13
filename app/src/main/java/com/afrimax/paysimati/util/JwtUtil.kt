package com.afrimax.paysimati.util

import com.afrimax.paysimati.BuildConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

object JwtUtil {
    fun generateToken(secret: String): String {
        val algorithm = Algorithm.HMAC256(secret)
        return JWT.create().withClaim("stage", BuildConfig.STAGE).sign(algorithm)
    }
}