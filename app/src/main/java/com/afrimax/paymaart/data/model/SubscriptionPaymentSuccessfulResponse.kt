package com.afrimax.paymaart.data.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class SubscriptionPaymentSuccessfulResponse(
    @SerializedName("data")
    val subscriptionPaymentDetails: SubscriptionPaymentDetails,
    @SerializedName("success_status")
    val successStatus: Boolean
)

data class SubscriptionPaymentDetails(
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("end_date")
    val endDate: Long,
    @SerializedName("membership")
    val membership: String?,
    @SerializedName("receiver_id")
    val receiverId: String?,
    @SerializedName("receiver_name")
    val receiverName: String?,
    @SerializedName("sender_id")
    val senderId: String?,
    @SerializedName("sender_name")
    val senderName: String?,
    @SerializedName("start_date")
    val startDate: Long,
    @SerializedName("transaction_amount")
    val transactionAmount: Double,
    @SerializedName("transaction_fee")
    val transactionFee: Double,
    @SerializedName("transaction_id")
    val transactionId: String?,
    @SerializedName("vat")
    val vat: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(createdAt)
        parcel.writeLong(endDate)
        parcel.writeString(membership)
        parcel.writeString(receiverId)
        parcel.writeString(receiverName)
        parcel.writeString(senderId)
        parcel.writeString(senderName)
        parcel.writeLong(startDate)
        parcel.writeDouble(transactionAmount)
        parcel.writeDouble(transactionFee)
        parcel.writeString(transactionId)
        parcel.writeDouble(vat)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscriptionPaymentDetails> {
        override fun createFromParcel(parcel: Parcel): SubscriptionPaymentDetails {
            return SubscriptionPaymentDetails(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionPaymentDetails?> {
            return arrayOfNulls(size)
        }
    }
}