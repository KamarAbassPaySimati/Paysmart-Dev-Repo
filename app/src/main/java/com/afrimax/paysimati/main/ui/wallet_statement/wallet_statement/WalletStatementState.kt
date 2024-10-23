package com.afrimax.paysimati.main.ui.wallet_statement.wallet_statement

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WalletStatementState(
    val selectedOption: Int
) : Parcelable