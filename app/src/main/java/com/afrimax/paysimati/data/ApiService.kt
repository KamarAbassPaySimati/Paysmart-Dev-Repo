package com.afrimax.paysimati.data

import com.afrimax.paysimati.data.model.ApproveUserRequest
import com.afrimax.paysimati.data.model.CashOutApiResponse
import com.afrimax.paysimati.data.model.CashOutRequestBody
import com.afrimax.paysimati.data.model.CreateUserRequestBody
import com.afrimax.paysimati.data.model.CreateUserResponse
import com.afrimax.paysimati.data.model.DeclineMerchantRequest
import com.afrimax.paysimati.data.model.DeclineMerchantResponse
import com.afrimax.paysimati.data.model.DefaultResponse
import com.afrimax.paysimati.data.model.DeleteAccountReqRequest
import com.afrimax.paysimati.data.model.FcmTokenRequest
import com.afrimax.paysimati.data.model.FlagTransactionRequest
import com.afrimax.paysimati.data.model.GetAfrimaxPlansResponse
import com.afrimax.paysimati.data.model.GetInstitutesResponse
import com.afrimax.paysimati.data.model.GetSharedSecretRequest
import com.afrimax.paysimati.data.model.GetSharedSecretResponse
import com.afrimax.paysimati.data.model.GetTaxForPayToMerchantResponse
import com.afrimax.paysimati.data.model.GetTaxForPayToRegisteredPersonResponse
import com.afrimax.paysimati.data.model.GetTaxForPayToUnRegisteredPersonResponse
import com.afrimax.paysimati.data.model.GetTransactionDetailsResponse
import com.afrimax.paysimati.data.model.GetUserKycDataResponse
import com.afrimax.paysimati.data.model.HomeScreenResponse
import com.afrimax.paysimati.data.model.KycSaveAddressDetailsRequest
import com.afrimax.paysimati.data.model.KycSaveCustomerPreferenceRequest
import com.afrimax.paysimati.data.model.KycSaveIdentityDetailRequest
import com.afrimax.paysimati.data.model.KycSavePersonalDetailRequest
import com.afrimax.paysimati.data.model.MembershipPlansResponse
import com.afrimax.paysimati.data.model.MerchantRequestPay
import com.afrimax.paysimati.data.model.MerchantRequestResponse
import com.afrimax.paysimati.data.model.PayMerchantRequest
import com.afrimax.paysimati.data.model.PayMerchantResponse
import com.afrimax.paysimati.data.model.PayPersonRequestBody
import com.afrimax.paysimati.data.model.PayPersonResponse
import com.afrimax.paysimati.data.model.PayToAfrimaxRequestBody
import com.afrimax.paysimati.data.model.PayToAfrimaxResponse
import com.afrimax.paysimati.data.model.PayToRegisteredPersonApiResponse
import com.afrimax.paysimati.data.model.PayToRegisteredPersonRequest
import com.afrimax.paysimati.data.model.PayToUnRegisteredPersonRequest
import com.afrimax.paysimati.data.model.PayToUnRegisteredPersonResponse
import com.afrimax.paysimati.data.model.PersonTransactions
import com.afrimax.paysimati.data.model.chat.PreviousChatResponse
import com.afrimax.paysimati.data.model.RefundRequestResponse
import com.afrimax.paysimati.data.model.ResendCredentialsRequest
import com.afrimax.paysimati.data.model.SaveBasicDetailsSelfKycRequest
import com.afrimax.paysimati.data.model.SaveIdentitySimplifiedToFullRequest
import com.afrimax.paysimati.data.model.SaveInfoSimplifiedToFullRequest
import com.afrimax.paysimati.data.model.SaveNewAddressDetailsSelfKycRequest
import com.afrimax.paysimati.data.model.SaveNewIdentityDetailsSelfKycRequest
import com.afrimax.paysimati.data.model.SaveNewInfoDetailsSelfKycRequest
import com.afrimax.paysimati.data.model.SearchMerchantByLocation
import com.afrimax.paysimati.data.model.SearchUsersDataResponse
import com.afrimax.paysimati.data.model.SecurityQuestionsResponse
import com.afrimax.paysimati.data.model.SelfKycDetailsResponse
import com.afrimax.paysimati.data.model.SendForgotOtpResponse
import com.afrimax.paysimati.data.model.SendOtpForEditSelfKycRequest
import com.afrimax.paysimati.data.model.SendOtpForEditSelfKycResponse
import com.afrimax.paysimati.data.model.SendOtpRequestBody
import com.afrimax.paysimati.data.model.SendOtpResponse
import com.afrimax.paysimati.data.model.SubscriptionDetailsRequestBody
import com.afrimax.paysimati.data.model.SubscriptionDetailsResponse
import com.afrimax.paysimati.data.model.SubscriptionPaymentRequestBody
import com.afrimax.paysimati.data.model.SubscriptionPaymentSuccessfulResponse
import com.afrimax.paysimati.data.model.TransactionDetailsResponse
import com.afrimax.paysimati.data.model.TransactionHistoryResponse
import com.afrimax.paysimati.data.model.UpdateAutoRenewalRequestBody
import com.afrimax.paysimati.data.model.UpdatePinOrPasswordRequest
import com.afrimax.paysimati.data.model.UpdatePinPasswordRequest
import com.afrimax.paysimati.data.model.ValidateAfrimaxIdResponse
import com.afrimax.paysimati.data.model.VerifyForgotOtpResponse
import com.afrimax.paysimati.data.model.VerifyOtpForEditSelfKycRequest
import com.afrimax.paysimati.data.model.VerifyOtpForEditSelfKycResponse
import com.afrimax.paysimati.data.model.VerifyOtpRequestBody
import com.afrimax.paysimati.data.model.VerifyOtpResponse
import com.afrimax.paysimati.data.model.ViewWalletResponse
import com.afrimax.paysimati.data.model.MerchantProfileResponse
import com.afrimax.paysimati.data.model.ReportMerchantRequest
import com.afrimax.paysimati.data.model.ReportMerchantResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

private const val CUSTOMER_USER = "customer-user"
private const val BDD = "bdd"
private const val KYC_UPDATE = "kyc-update"
private const val PAYMAART = "paymaart"
private const val CUSTOMER = "customer"
private const val AFRIMAX = "afrimax"
private const val CASHIN_CASHOUT = "cashin-cashout"


interface ApiService {

    @GET("$CUSTOMER_USER/security-questions")
    fun getSecurityQuestions(): Call<SecurityQuestionsResponse>

    @POST("$CUSTOMER_USER/send-otp")
    fun sentOtp(@Body body: SendOtpRequestBody): Call<SendOtpResponse>

    @POST("$CUSTOMER_USER/verify-otp")
    fun verifyOtp(@Body body: VerifyOtpRequestBody): Call<VerifyOtpResponse>

    @POST("$CUSTOMER_USER/register")
    fun registerCustomer(@Body body: CreateUserRequestBody): Call<CreateUserResponse>

    @POST("$CUSTOMER_USER/resend-credentials")
    fun resendCredentials(@Body body: ResendCredentialsRequest): Call<DefaultResponse>

    @GET("$CUSTOMER_USER/view-kyc")
    fun viewKyc(@Header("Authorization") header: String): Call<GetUserKycDataResponse>

    @POST("$CUSTOMER_USER/create-kyc")
    fun saveCustomerKYCPreference(
        @Header("Authorization") header: String, @Body body: KycSaveCustomerPreferenceRequest
    ): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/create-kyc")
    fun saveCustomerAddressDetails(
        @Header("Authorization") header: String, @Body body: KycSaveAddressDetailsRequest
    ): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/create-kyc")
    fun saveCustomerIdentityDetails(
        @Header("Authorization") header: String, @Body body: KycSaveIdentityDetailRequest
    ): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/create-kyc")
    fun saveCustomerPersonalDetails(
        @Header("Authorization") header: String, @Body body: KycSavePersonalDetailRequest
    ): Call<DefaultResponse>

    @GET("admin-users/list-institution")
    fun getInstitutes(@Query("search") search: String): Call<GetInstitutesResponse>

    @GET("$CUSTOMER_USER/get-home-screen-data")
    fun getHomeScreenData(@Header("Authorization") header: String): Call<HomeScreenResponse>

    @POST("$CUSTOMER_USER/delete-request")
    fun deleteAccountRequest(
        @Header("Authorization") header: String, @Body body: DeleteAccountReqRequest
    ): Call<DefaultResponse>

    @GET("$CUSTOMER_USER/request-otp")
    fun sendForgotOtp(
        @Query("email_address") emailAddress: String, @Query("type") type: String
    ): Call<SendForgotOtpResponse>

    @GET("$CUSTOMER_USER/validate-otp")
    fun verifyForgotOtp(
        @Query(value = "otp") otp: String,
        @Query("encrypted_otp") encryptedOtp: String,
        @Query("user_id") userId: String
    ): Call<VerifyForgotOtpResponse>

    @POST("$CUSTOMER_USER/forgot-password")
    fun updatePinPassword(@Body body: UpdatePinPasswordRequest): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/update-password")
    fun updatePinOrPassword(
        @Header("Authorization") header: String, @Body body: UpdatePinOrPasswordRequest
    ): Call<DefaultResponse>

    @GET("$CUSTOMER_USER/view-membership-benefits")
    fun getMembershipDetails(@Header("Authorization") header: String): Call<MembershipPlansResponse>

    @GET("$CUSTOMER_USER/view-self-kyc-customer")
    suspend fun getSelfKycDetails(
        @Query(value = "password") password: String, @Header("Authorization") header: String
    ): Response<SelfKycDetailsResponse>

    @POST("$KYC_UPDATE/send-otp-mobile-customer")
    fun sendOtpForEditSelfKyc(
        @Header("Authorization") header: String, @Body body: SendOtpForEditSelfKycRequest
    ): Call<SendOtpForEditSelfKycResponse>

    @GET("$KYC_UPDATE/view-kyc-data-mobile-customer")
    fun getSelfKycUserData(@Header("Authorization") header: String): Call<SelfKycDetailsResponse>

    @POST("$KYC_UPDATE/update/basicDetails-customer-self")
    fun saveBasicDetailsSelfKyc(
        @Header("Authorization") header: String, @Body body: SaveBasicDetailsSelfKycRequest
    ): Call<DefaultResponse>

    @POST("$KYC_UPDATE/verify-otp-mobile-customer")
    fun verifyOtpForEditSelfKyc(
        @Header("Authorization") header: String, @Body body: VerifyOtpForEditSelfKycRequest
    ): Call<VerifyOtpForEditSelfKycResponse>

    @POST("$KYC_UPDATE/update/addressDetails-customer-self")
    fun saveNewAddressDetailsSelfKyc(
        @Header("Authorization") header: String, @Body body: SaveNewAddressDetailsSelfKycRequest
    ): Call<DefaultResponse>

    @POST("$KYC_UPDATE/update/documentsDetails-customer-self")
    fun saveNewIdentityDetailsSelfKyc(
        @Header("Authorization") header: String, @Body body: SaveNewIdentityDetailsSelfKycRequest
    ): Call<DefaultResponse>

    @POST("$KYC_UPDATE/update/infoDetails-customer-self")
    fun saveNewInfoDetailsSelfKyc(
        @Header("Authorization") header: String, @Body body: SaveNewInfoDetailsSelfKycRequest
    ): Call<DefaultResponse>

    @POST("$KYC_UPDATE/update/convert-kyc-mobile-customer-self")
    fun switchToFullKyc(@Header("Authorization") header: String): Call<DefaultResponse>

    @POST("$KYC_UPDATE/update/simplifiedtofull-mobile-customer-self")
    fun saveIdentitySimplifiedToFull(
        @Header("Authorization") header: String, @Body body: SaveIdentitySimplifiedToFullRequest
    ): Call<DefaultResponse>

    @POST("$KYC_UPDATE/update/simplifiedtofull-mobile-customer-self")
    fun saveInfoSimplifiedToFull(
        @Header("Authorization") header: String, @Body body: SaveInfoSimplifiedToFullRequest
    ): Call<DefaultResponse>

    @GET("$CUSTOMER_USER/view-wallet")
    suspend fun viewWallet(
        @Header("Authorization") header: String, @Query("password") password: String
    ): Response<ViewWalletResponse>

    @POST("$PAYMAART/$CUSTOMER/subscription-details")
    fun getSubscriptionDetails(
        @Header("Authorization") header: String, @Body body: SubscriptionDetailsRequestBody
    ): Call<SubscriptionDetailsResponse>

    @POST("$PAYMAART/$CUSTOMER/subscription-payment")
    suspend fun subscriptionPayment(
        @Header("Authorization") header: String, @Body body: SubscriptionPaymentRequestBody
    ): Response<SubscriptionPaymentSuccessfulResponse>

    @PATCH("$PAYMAART/$CUSTOMER/update-auto-renew")
    fun updateAutoRenewal(
        @Header("Authorization") header: String, @Body body: UpdateAutoRenewalRequestBody
    ): Call<Unit>

    @GET("$AFRIMAX/cmr/customer/{id}")
    fun validateAfrimaxId(
        @Header("Authorization") header: String, @Path("id") afrimaxId: Number
    ): Call<ValidateAfrimaxIdResponse>

    @GET("$AFRIMAX/cmr/plans")
    fun getAfrimaxPlans(
        @Header("Authorization") header: String, @Query("page") page: Int
    ): Call<GetAfrimaxPlansResponse>

    @GET("chats/customer-messages")
    suspend fun getPreviousChat(
        @Header("Authorization") header: String,
        @Query("receiver_id") receiverId: String,
        @Query("page") page: Int
    ): Response<PreviousChatResponse>


    @POST("$AFRIMAX/cmr/payment")
    suspend fun payToAfrimax(
        @Header("Authorization") header: String, @Body body: PayToAfrimaxRequestBody
    ): Response<PayToAfrimaxResponse>

    @POST("flag-transaction/flag-transaction-customer")
    fun flagTransaction(
        @Header("Authorization") header: String, @Body body: FlagTransactionRequest
    ): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/add-notification-id")
    fun storeFcmToken(
        @Header("Authorization") header: String, @Body body: FcmTokenRequest
    ): Call<DefaultResponse>

    @POST("$CUSTOMER_USER/delete-notification-id")
    fun deleteFcmToken(
        @Header("Authorization") header: String, @Body body: FcmTokenRequest
    ): Call<DefaultResponse>

    @GET("$CASHIN_CASHOUT/search-cashout-customer")
    fun getAgentsForSelfCashOut(
        @Header("Authorization") header: String,
        @Query("page") page: Int,
        @Query("search") search: String?
    ): Call<SearchUsersDataResponse>


    @POST("$CASHIN_CASHOUT/request-cashout-customer")
    suspend fun cashOut(
        @Header("Authorization") header: String, @Body body: CashOutRequestBody
    ): Response<CashOutApiResponse>

    @GET("$CASHIN_CASHOUT/calc-fee")
    fun getTransactionDetails(
        @Header("Authorization") header: String, @Query("amount") amount: String
    ): Call<TransactionDetailsResponse>

    @GET("$CUSTOMER_USER/list-refund-request")
    fun getRefundRequests(
        @Header("Authorization") header: String,
        @Query("page") page: Int? = 1,
        @Query("status") status: String? = "",
        @Query("time") time: Int? = 60
    ): Call<RefundRequestResponse>

    @GET("agent-users/customer/list-transaction")
    fun getTransactionHistory(
        @Header("Authorization") header: String,
        @Query("page") page: Int?,
        @Query("search") search: String?,
        @Query("type") type: String?,
        @Query("time") time: Int?
    ): Call<TransactionHistoryResponse>

    @GET("agent-users/customer/view-transaction")
    fun getTransactionDetailsApi(
        @Header("Authorization") header: String, @Query("transaction_id") transactionId: String
    ): Call<GetTransactionDetailsResponse>

    @POST("bank-transactions/pay-unregister")
    suspend fun getTaxForPayToUnRegisteredPerson(
        @Header("Authorization") header: String, @Body body: PayToUnRegisteredPersonRequest
    ): Response<GetTaxForPayToUnRegisteredPersonResponse>

    @POST("bank-transactions/pay-unregister")
    suspend fun payToUnRegisteredPerson(
        @Header("Authorization") header: String, @Body body: PayToUnRegisteredPersonRequest
    ): Response<PayToUnRegisteredPersonResponse>

    @GET("$CUSTOMER_USER/search-by-id")
    suspend fun searchUsersByPaymaartCredentials(
        @Header("Authorization") header: String,
        @Query("page") page: Int = 1,
        @Query("search") search: String?
    ): Response<PayPersonResponse>

    @POST("$CUSTOMER_USER/search-by-phone")
    suspend fun searchUsersByPhoneCredentials(
        @Header("Authorization") header: String, @Body body: PayPersonRequestBody
    ): Response<PayPersonResponse>

    @GET("$CUSTOMER_USER/view-transaction")
    fun viewPersonTransactionHistory(
        @Header("Authorization") header: String,
        @Query("paymaart_id") paymaartId: String,
        @Query("page") page: Int = 1
    ): Call<PersonTransactions>

    @GET("$CUSTOMER_USER/recent-transaction")
    fun getPersonRecentTransactionList(
        @Header("Authorization") header: String, @Query("page") page: Int = 1
    ): Call<PayPersonResponse>


    @GET("$CUSTOMER_USER/recent-transactions")
    fun getMerchantTransactionList(
        @Header("Authorization") header: String, @Query("page") page: Int = 1
    ): Call<PayMerchantResponse>

    @GET("$CUSTOMER_USER/merchant-detail")
    fun getMerchantProfile(
        @Header("Authorization") header: String,
        @Query("merchant_id") merchant_id:String
    ):Call<MerchantProfileResponse>

    @GET("$CUSTOMER_USER/recent-transactions")
    fun searchMerchantById(
        @Header("Authorization") header: String,
        @Query("search") search: String,
        @Query("page") page: Int = 1
    ): Call<PayMerchantResponse>

    @GET("$CUSTOMER_USER/find-merchants")
    fun searchMerchantByLocation(
        @Header("Authorization") header: String,
        @Query("location") search: String,
        @Query("trading_type") tradingType: String? = null,
        @Query("page") page: Int = 1
    ): Call<SearchMerchantByLocation>


    @POST("bank-transactions/customer/payment-details")
    suspend fun getTaxForPayToRegisteredPerson(
        @Header("Authorization") header: String, @Body body: PayToRegisteredPersonRequest
    ): Response<GetTaxForPayToRegisteredPersonResponse>


    @POST("chats/pay-merchant")
    suspend fun  getTaxForMechant(
        @Header("Authorization") header: String, @Body body: PayMerchantRequest
    ): Response<GetTaxForPayToMerchantResponse>

    @POST("bank-transactions/customer/pay-customer")
    suspend fun payToRegisteredPerson(
        @Header("Authorization") header: String, @Body body: PayToRegisteredPersonRequest
    ): Response<PayToRegisteredPersonApiResponse>

    @POST("chats/pay-request")
    suspend fun payMerchantRequest(
        @Header("Authorization") header: String, @Body body: MerchantRequestPay
    ):Response<MerchantRequestResponse>

    @POST("chats/decline")
    suspend fun declineMerchantRequest(
        @Header("Authorization") header: String, @Body body: DeclineMerchantRequest
    ):Response<DeclineMerchantResponse>

    @POST("$CUSTOMER_USER//report-merchant")
    suspend fun reportMerchant(
        @Header("Authorization") header: String, @Body body: ReportMerchantRequest
    ): Response<ReportMerchantResponse>

    //For BDD purpose
    @POST("$BDD/customer-fetch-mfa")
    fun getSharedSecret(
        @Body body: GetSharedSecretRequest, @Header("Authorization") header: String
    ): Call<GetSharedSecretResponse>

    @POST("$BDD/approve")
    fun approveUser(
        @Body body: ApproveUserRequest, @Header("Authorization") header: String
    ): Call<DefaultResponse>
}