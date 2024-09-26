package com.afrimax.paysimati.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class PayToRegisteredPersonApiResponse(
    @SerializedName("data") val data: PayToRegisteredPersonResponse,
    @SerializedName("success_status") val successStatus: Boolean = false
)

data class PayToRegisteredPersonResponse(
    @SerializedName("created_at") val createdAt: Double = 0.0,
    @SerializedName("note") val note: String? = "",
    @SerializedName("receiver_id") val receiverId: String? = "",
    @SerializedName("receiver_name") val receiverName: String? = "",
    @SerializedName("sender_id") val senderId: String? = "",
    @SerializedName("sender_name") val senderName: String? = "",
    @SerializedName("transaction_amount") val transactionAmount: Double = 0.0,
    @SerializedName("transaction_fee") val transactionFee: Double = 0.0,
    @SerializedName("transaction_id") val transactionId: String? = "",
    @SerializedName("vat") val vat: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(createdAt)
        parcel.writeString(note)
        parcel.writeString(receiverId)
        parcel.writeString(receiverName)
        parcel.writeString(senderId)
        parcel.writeString(senderName)
        parcel.writeDouble(transactionAmount)
        parcel.writeDouble(transactionFee)
        parcel.writeString(transactionId)
        parcel.writeDouble(vat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PayToRegisteredPersonResponse> {
        override fun createFromParcel(parcel: Parcel): PayToRegisteredPersonResponse {
            return PayToRegisteredPersonResponse(parcel)
        }

        override fun newArray(size: Int): Array<PayToRegisteredPersonResponse?> {
            return arrayOfNulls(size)
        }
    }
}