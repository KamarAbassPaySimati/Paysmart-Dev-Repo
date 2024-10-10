package com.afrimax.paysimati.data

import com.afrimax.paysimati.BuildConfig
import com.afrimax.paysimati.data.model.RecaptchaRequestBody
import com.afrimax.paysimati.data.model.RecaptchaResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object RecaptchaRetrofitClient {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.google.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object RecaptchaApiClient{
    val recaptchaApiService: RecaptchaApiService by lazy {
        RecaptchaRetrofitClient.retrofit.create(RecaptchaApiService::class.java)
    }
}

interface RecaptchaApiService{
    @POST("recaptcha/api/siteverify")
    fun verifyUserRecaptcha(@Body body: RecaptchaRequestBody): Call<RecaptchaResponse>
}