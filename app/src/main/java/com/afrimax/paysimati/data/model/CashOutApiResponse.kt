package com.afrimax.paysimati.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CashOutApiResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("data")
    val cashOutResponse: CashOutResponse,
)

data class CashOutResponse(
    @SerializedName("from_name")
    val fromName: String?,
    @SerializedName("from_paymaart_id")
    val fromPaymaartId: String?,
    @SerializedName("to_name")
    val toName: String?,
    @SerializedName("to_paymaart_id")
    val toPaymaartId: String?,
    @SerializedName("transaction_amount")
    val transactionAmount: String?,
    @SerializedName("tax")
    val tax: Float,
    @SerializedName("VAT")
    val vat: Float,
    @SerializedName("transaction_id")
    val transactionId: String?,
    @SerializedName("balance")
    val balance: String?,
    @SerializedName("date_time")
    val dateTime: Long,
    @SerializedName("note")
    val note: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fromName)
        parcel.writeString(fromPaymaartId)
        parcel.writeString(toName)
        parcel.writeString(toPaymaartId)
        parcel.writeString(transactionAmount)
        parcel.writeFloat(tax)
        parcel.writeFloat(vat)
        parcel.writeString(transactionId)
        parcel.writeString(balance)
        parcel.writeLong(dateTime)
        parcel.writeString(note)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CashOutResponse> {
        override fun createFromParcel(parcel: Parcel): CashOutResponse {
            return CashOutResponse(parcel)
        }

        override fun newArray(size: Int): Array<CashOutResponse?> {
            return arrayOfNulls(size)
        }
    }
}

data class CashOutRequestBody(
    @SerializedName("requested_to")
    val requestedTo: String,
    @SerializedName("transaction_amount")
    val transactionAmount: String,
    @SerializedName("password")
    val password: String? = null
)
