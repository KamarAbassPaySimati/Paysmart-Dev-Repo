package com.afrimax.paysimati.ui.membership

import android.os.Parcel
import android.os.Parcelable

data class RenewalPlans(
    val membershipType: String?,
    val referenceNumber: String?,
    val planPrice: String?,
    val planValidity: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(membershipType)
        parcel.writeString(referenceNumber)
        parcel.writeString(planPrice)
        parcel.writeString(planValidity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RenewalPlans> {
        override fun createFromParcel(parcel: Parcel): RenewalPlans {
            return RenewalPlans(parcel)
        }

        override fun newArray(size: Int): Array<RenewalPlans?> {
            return arrayOfNulls(size)
        }
    }
}
