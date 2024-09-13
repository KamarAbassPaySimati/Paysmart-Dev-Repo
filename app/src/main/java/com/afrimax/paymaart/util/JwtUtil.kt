package com.afrimax.paymaart.util

import com.afrimax.paymaart.BuildConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

object JwtUtil {
    fun generateToken(secret: String): String {
        val algorithm = Algorithm.HMAC256(secret)
        return JWT.create().withClaim("stage", BuildConfig.STAGE).sign(algorithm)
    }
}