package com.afrimax.paymaart.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class PayToUnRegisteredPersonResponse(
    @SerializedName("data") val data: PayUnRegisteredPersonResponse,
    @SerializedName("message") val message: String = ""
)

data class PayUnRegisteredPersonResponse(
    @SerializedName("amount") val amount: String? = "",
    @SerializedName("created_at") val createdAt: Double = 0.0,
    @SerializedName("note") val note: String? = "",
    @SerializedName("phone_number") val phoneNumber: String? = "",
    @SerializedName("receiver_name") val receiverName: String? = "",
    @SerializedName("sender_id") val senderId: String? = "",
    @SerializedName("sender_name") val senderName: String? = "",
    @SerializedName("transaction_fees") val transactionFees: String? = "",
    @SerializedName("transaction_id") val transactionId: String? = "",
    @SerializedName("vat_amount") val vatAmount: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(amount)
        parcel.writeDouble(createdAt)
        parcel.writeString(note)
        parcel.writeString(phoneNumber)
        parcel.writeString(receiverName)
        parcel.writeString(senderId)
        parcel.writeString(senderName)
        parcel.writeString(transactionFees)
        parcel.writeString(transactionId)
        parcel.writeString(vatAmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PayUnRegisteredPersonResponse> {
        override fun createFromParcel(parcel: Parcel): PayUnRegisteredPersonResponse {
            return PayUnRegisteredPersonResponse(parcel)
        }

        override fun newArray(size: Int): Array<PayUnRegisteredPersonResponse?> {
            return arrayOfNulls(size)
        }
    }
}