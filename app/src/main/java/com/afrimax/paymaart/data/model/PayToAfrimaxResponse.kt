package com.afrimax.paymaart.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PayToAfrimaxResponse(
    val message: String,
    @SerializedName("data")
    val payAfrimaxResponse: PayAfrimaxResponse,
    val status: Boolean,
    val time: String,
)

data class PayAfrimaxResponse(
    @SerializedName("from_name") val fromName: String,
    @SerializedName("from_paymaart_id") val fromPaymaartId: String,
    @SerializedName("to_name") val toName: String,
    @SerializedName("to_paymaart_id") val toPaymaartId: String,
    @SerializedName("obo_paymaart_name") val oboPaymaartName: String,
    @SerializedName("obo_paymaart_id") val oboPaymaartId: String?,
    @SerializedName("obo_phone_number") val oboPhoneNumber: String?,
    @SerializedName("afrimax_name") val afrimaxName: String,
    @SerializedName("afrimax_id") val afrimaxId: String,
    @SerializedName("transaction_amount") val transactionAmount: Double,
    @SerializedName("tax") val tax: String,
    @SerializedName("VAT") val vat: String,
    @SerializedName("commission_earned") val commissionEarned: String,
    @SerializedName("transaction_id") val transactionId: String,
    @SerializedName("date_time") val dateTime: Long,
    var plan: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fromName)
        parcel.writeString(fromPaymaartId)
        parcel.writeString(toName)
        parcel.writeString(toPaymaartId)
        parcel.writeString(oboPaymaartName)
        parcel.writeString(oboPaymaartId)
        parcel.writeString(oboPhoneNumber)
        parcel.writeString(afrimaxName)
        parcel.writeString(afrimaxId)
        parcel.writeDouble(transactionAmount)
        parcel.writeString(tax)
        parcel.writeString(vat)
        parcel.writeString(commissionEarned)
        parcel.writeString(transactionId)
        parcel.writeLong(dateTime)
        parcel.writeString(plan)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PayAfrimaxResponse> {
        override fun createFromParcel(parcel: Parcel): PayAfrimaxResponse {
            return PayAfrimaxResponse(parcel)
        }

        override fun newArray(size: Int): Array<PayAfrimaxResponse?> {
            return arrayOfNulls(size)
        }
    }
}


data class PayToAfrimaxRequestBody(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("comment")
    val comment: String? = null,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("customer_name")
    val customerName: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("paymaart_id")
    val paymaartId: String,
    @SerializedName("paymaart_name")
    val paymaartName: String
)

data class PayToAfrimaxErrorResponse(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("time")
    val time: String
)