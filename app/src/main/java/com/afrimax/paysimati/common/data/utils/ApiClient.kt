package com.afrimax.paysimati.common.data.utils

import com.afrimax.paymaart.agents.common.data.utils.NetworkInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApiClient @Inject constructor(private val networkInterceptor: NetworkInterceptor) {

    val instance: OkHttpClient by lazy {
        OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(networkInterceptor).build()
    }
}