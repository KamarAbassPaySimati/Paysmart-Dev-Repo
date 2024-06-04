package com.afrimax.paymaart.data

import com.afrimax.paymaart.data.model.SendOtpRequestBody
import com.afrimax.paymaart.data.model.SendOtpResponse
import com.afrimax.paymaart.data.model.VerifyOtpRequestBody
import com.afrimax.paymaart.data.model.VerifyOtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("")
    fun sentOtp(@Body body: SendOtpRequestBody): Call<SendOtpResponse>

    @POST("")
    fun verifyOtp(@Body body: VerifyOtpRequestBody): Call<VerifyOtpResponse>
}