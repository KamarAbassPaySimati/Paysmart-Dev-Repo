package com.afrimax.paymaart.ui.utils.interfaces

import com.afrimax.paymaart.data.model.WalletData

interface HomeInterface {
    fun onClickViewBalance(viewWalletScope: String, data: WalletData?)
}