package com.afrimax.paymaart.data.model

import com.google.gson.annotations.SerializedName

data class GetTransactionDetailsResponse(
    @SerializedName("data") val transactionDetail: TransactionDetail,
    @SerializedName("success_status") val successStatus: Boolean = false
)

data class TransactionDetail(
    @SerializedName("bank_id") val bankId: String? = "",
    @SerializedName("closing_balance") val closingBalance: String? = null,
    @SerializedName("commission") val commission: String? = null,
    @SerializedName("created_at") val createdAt: Double? = null,
    @SerializedName("entered_by") val enteredBy: String? = null,
    @SerializedName("entered_by_name") val enteredByName: String? = null,
    @SerializedName("extras") val extras: String? = null,
    @SerializedName("flagged") val flagged: Boolean,
    @SerializedName("id") val id: String = "",
    @SerializedName("membership_data_id") val membershipDataId: String? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("pop_file_key") val popFileKey: String? = null,
    @SerializedName("pop_file_ref_no") val popFileRefNo: String? = null,
    @SerializedName("receiver_closing_balance") val receiverClosingBalance: String? = null,
    @SerializedName("receiver_id") val receiverId: String? = "",
    @SerializedName("receiver_name") val receiverName: String? = "",
    @SerializedName("receiver_profile_pic") val receiverProfilePic: String? = "",
    @SerializedName("sender_closing_balance") val senderClosingBalance: String? = null,
    @SerializedName("sender_id") val senderId: String? = "",
    @SerializedName("sender_name") val senderName: String? = "",
    @SerializedName("sender_profile_pic") val senderProfilePic: String? = null,
    @SerializedName("transaction_amount") val transactionAmount: String? = null,
    @SerializedName("transaction_code") val transactionCode: String = "",
    @SerializedName("transaction_fee") val transactionFee: String? = null,
    @SerializedName("transaction_id") val transactionId: String,
    @SerializedName("transaction_type") val transactionType: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("vat") val vat: String? = null,
    @SerializedName("bank_details") val bankDetails: String? = null,
    @SerializedName("membership_data") val membershipData: String? = null,
    @SerializedName("total_value") val totalValue:String? = null
)


data class Extra(
    @SerializedName("afx_customer_id")
    val afrimaxCustomerId: Int,
    @SerializedName("afx_customer_name")
    val afrimaxCustomerName: String,
    @SerializedName("afx_plan_name")
    val afrimaxPlanName: String? = null,
    @SerializedName("afx_customer_payment_id")
    val afrimaxCustomerPaymentId: String? = null,
)

data class Membership(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("membership")
    val membershipName: String? = null,
    @SerializedName("validity")
    val membershipShipValidity: Int? = null,
    @SerializedName("membership_start")
    val membershipStart: Long? = null,
    @SerializedName("membership_expiry")
    val membershipExpiry: Long? = null,
)