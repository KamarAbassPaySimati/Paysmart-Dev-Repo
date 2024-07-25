package com.afrimax.paymaart.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class SearchUsersDataResponse(
    @SerializedName("success_status")
    val successStatus: Boolean,
    val totalPages: Int,
    val limit: Int,
    val totalRecords: Int,
    val currentPage: Int,
    val nextPage: Int?,
    val previousPage: Int?,
    val data: ArrayList<IndividualSearchUserData>
)

data class IndividualSearchUserData(
    @SerializedName("paymaart_id")
    val paymaartId: String,
    val name: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
    val membership:String? = "",
    val viewType: String? = "" //viewType is required to show pager lottie
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(paymaartId)
        parcel.writeString(name)
        parcel.writeString(countryCode)
        parcel.writeString(phoneNumber)
        parcel.writeString(membership)
        parcel.writeString(viewType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IndividualSearchUserData> {
        override fun createFromParcel(parcel: Parcel): IndividualSearchUserData {
            return IndividualSearchUserData(parcel)
        }

        override fun newArray(size: Int): Array<IndividualSearchUserData?> {
            return arrayOfNulls(size)
        }
    }
}
