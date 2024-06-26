package com.afrimax.paymaart.data

import com.afrimax.paymaart.data.model.CreateUserRequestBody
import com.afrimax.paymaart.data.model.CreateUserResponse
import com.afrimax.paymaart.data.model.DefaultResponse
import com.afrimax.paymaart.data.model.DeleteAccountReqRequest
import com.afrimax.paymaart.data.model.GetInstitutesResponse
import com.afrimax.paymaart.data.model.GetSharedSecretRequest
import com.afrimax.paymaart.data.model.GetSharedSecretResponse
import com.afrimax.paymaart.data.model.GetUserKycDataResponse
import com.afrimax.paymaart.data.model.HomeScreenResponse
import com.afrimax.paymaart.data.model.KycSaveAddressDetailsRequest
import com.afrimax.paymaart.data.model.ResendCredentialsRequest
import com.afrimax.paymaart.data.model.KycSaveCustomerPreferenceRequest
import com.afrimax.paymaart.data.model.KycSaveIdentityDetailRequest
import com.afrimax.paymaart.data.model.KycSavePersonalDetailRequest
import com.afrimax.paymaart.data.model.SecurityQuestionsResponse
import com.afrimax.paymaart.data.model.SendForgotOtpResponse
import com.afrimax.paymaart.data.model.SendOtpRequestBody
import com.afrimax.paymaart.data.model.SendOtpResponse
import com.afrimax.paymaart.data.model.UpdatePinOrPasswordRequest
import com.afrimax.paymaart.data.model.UpdatePinPasswordRequest
import com.afrimax.paymaart.data.model.VerifyForgotOtpResponse
import com.afrimax.paymaart.data.model.VerifyOtpRequestBody
import com.afrimax.paymaart.data.model.VerifyOtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

private const val CUSTOMER_USER = "customer-user"
private const val BDD = "bdd"
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

    @POST("$CUSTOMER_USER/create-kyc")
    fun saveCustomerKYCPreference(@Header("Authorization") header: String, @Body body: KycSaveCustomerPreferenceRequest): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/create-kyc")
    fun saveCustomerAddressDetails(@Header("Authorization") header: String, @Body body: KycSaveAddressDetailsRequest): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/create-kyc")
    fun saveCustomerIdentityDetails(@Header("Authorization") header: String, @Body body: KycSaveIdentityDetailRequest): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/create-kyc")
    fun saveCustomerPersonalDetails(@Header("Authorization") header: String, @Body body: KycSavePersonalDetailRequest): Call<DefaultResponse>

    @GET("admin-users/list-institution")
    fun getInstitutes(@Query("search") search: String): Call<GetInstitutesResponse>

    @GET("$CUSTOMER_USER/get-home-screen-data")
    fun getHomeScreenData(@Header("Authorization") header: String): Call<HomeScreenResponse>

    @POST("$CUSTOMER_USER/delete-request")
    fun deleteAccountRequest(@Header("Authorization") header: String, @Body body: DeleteAccountReqRequest): Call<DefaultResponse>

    @GET("$CUSTOMER_USER/request-otp")
    fun sendForgotOtp(@Query("email_address") emailAddress: String, @Query("type") type: String): Call<SendForgotOtpResponse>

    @GET("$CUSTOMER_USER/validate-otp")
    fun verifyForgotOtp(@Query(value = "otp") otp: String, @Query("encrypted_otp") encryptedOtp: String, @Query("user_id") userId: String): Call<VerifyForgotOtpResponse>

    @POST("$CUSTOMER_USER/forgot-password")
    fun updatePinPassword(@Body body: UpdatePinPasswordRequest): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/update-password")
    fun updatePinOrPassword(@Header("Authorization") header: String, @Body body: UpdatePinOrPasswordRequest): Call<DefaultResponse>

    //For BDD purpose
    @POST("$BDD/customer-fetch-mfa")
    fun getSharedSecret(@Body body: GetSharedSecretRequest, @Header("Authorization") header: String): Call<GetSharedSecretResponse>
}