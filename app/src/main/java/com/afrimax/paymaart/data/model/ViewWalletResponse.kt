package com.afrimax.paymaart.data.model

import com.google.gson.annotations.SerializedName

data class ViewWalletResponse(
    @SerializedName("success_status")
    val successStatus: Boolean,
    val data: WalletData?,
)

data class WalletData(
    @SerializedName("account_balance")
    val accountBalance: String?,
)
