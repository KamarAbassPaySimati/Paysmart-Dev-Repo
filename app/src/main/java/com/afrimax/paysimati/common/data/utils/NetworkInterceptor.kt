package com.afrimax.paymaart.agents.common.data.utils


import com.afrimax.paysimati.common.data.utils.AppIdlingResource
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class NetworkInterceptor @Inject constructor(private val appIdlingResource: AppIdlingResource) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //Change app idle state to false
        appIdlingResource.registerBackgroundOperation { chain.proceed(chain.request()) }

        val request = chain.request()
        val response = chain.proceed(request)

        //Change app idle state back to true
        appIdlingResource.unregisterBackgroundOperation { chain.proceed(request) }

        return response
    }
}