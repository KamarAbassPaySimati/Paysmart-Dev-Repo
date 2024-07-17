package com.afrimax.paymaart.ui.membership

import android.os.Parcel
import android.os.Parcelable

data class MembershipPlanModel (
    val membershipType: String?,
    val validity: String?,
    val paymentType: String? = PaymentType.PREPAID.type,
    val renewalType: Boolean = false,
    val referenceNumber: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(membershipType)
        parcel.writeString(validity)
        parcel.writeString(paymentType)
        parcel.writeByte(if (renewalType) 1 else 0)
        parcel.writeString(referenceNumber)
    }

    companion object CREATOR : Parcelable.Creator<MembershipPlanModel> {
        override fun createFromParcel(parcel: Parcel): MembershipPlanModel {
            return MembershipPlanModel(parcel)
        }

        override fun newArray(size: Int): Array<MembershipPlanModel?> {
            return arrayOfNulls(size)
        }
    }
}

enum class PaymentType(val type: String){
    PREPAID("Prepaid"),
    POSTPAID("Postpaid")
}