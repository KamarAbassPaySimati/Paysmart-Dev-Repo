package com.afrimax.paysimati.data.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class SubscriptionDetailsResponse(
    @SerializedName("data") val subscriptionDetails: SubscriptionDetails,
    @SerializedName("success_status") val successStatus: Boolean
)

data class SubscriptionDetails(
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("transaction_fee") val transactionFee: Double,
    @SerializedName("vat") val vat: Double,
    @SerializedName("membership_start") val membershipStart: Long,
    @SerializedName("membership_expiry") val membershipExpiry: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(totalAmount)
        parcel.writeDouble(transactionFee)
        parcel.writeDouble(vat)
        parcel.writeLong(membershipStart)
        parcel.writeLong(membershipExpiry)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubscriptionDetails> {
        override fun createFromParcel(parcel: Parcel): SubscriptionDetails {
            return SubscriptionDetails(parcel)
        }

        override fun newArray(size: Int): Array<SubscriptionDetails?> {
            return arrayOfNulls(size)
        }
    }
}

data class SubscriptionDetailsRequestBody(
    @SerializedName("ref_no") val referenceNumber: String?,
    @SerializedName("sub_type") val subType: String?,
    @SerializedName("auto_renew") val autoRenew: Boolean
)