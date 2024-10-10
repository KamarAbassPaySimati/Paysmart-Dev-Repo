package com.afrimax.paysimati.ui.utils.interfaces

import com.afrimax.paysimati.data.model.WalletData

interface HomeInterface {
    fun onClickViewBalance(viewWalletScope: String, data: WalletData?)
}