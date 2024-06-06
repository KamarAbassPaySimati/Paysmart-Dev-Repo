package com.afrimax.paymaart.data

import com.afrimax.paymaart.data.model.CreateUserRequestBody
import com.afrimax.paymaart.data.model.CreateUserResponse
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.data.model.GetUserKycDataResponse
import com.afrimax.paymaart.data.model.ResendCredentialsRequest
import com.afrimax.paymaart.data.model.SecurityQuestionsResponse
import com.afrimax.paymaart.data.model.SendOtpRequestBody
import com.afrimax.paymaart.data.model.SendOtpResponse
import com.afrimax.paymaart.data.model.VerifyOtpRequestBody
import com.afrimax.paymaart.data.model.VerifyOtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
private const val CUSTOMER_USER = "customer-user"
interface ApiService {

    @GET("$CUSTOMER_USER/security-questions")
    fun getSecurityQuestions(): Call<SecurityQuestionsResponse>

    @POST("$CUSTOMER_USER/send-otp")
    fun sentOtp(@Body body: SendOtpRequestBody): Call<SendOtpResponse>

    @POST("$CUSTOMER_USER/verify-otp")
    fun verifyOtp(@Body body: VerifyOtpRequestBody): Call<VerifyOtpResponse>

    @POST("$CUSTOMER_USER/create")
    fun registerCustomer(@Body body: CreateUserRequestBody): Call<CreateUserResponse>

    @POST("$CUSTOMER_USER/resend-credentials")
    fun resendCredentials(@Body body: ResendCredentialsRequest): Call<DefaultResponse>

    @GET("$CUSTOMER_USER/view-kyc")
    fun viewKyc(@Header("Authorization") header: String): Call<GetUserKycDataResponse>
}