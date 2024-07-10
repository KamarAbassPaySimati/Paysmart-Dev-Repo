package com.afrimax.paymaart.ui.utils.interfaces

import com.afrimax.paymaart.data.model.WalletData

interface HomeInterface {
    //Communication from sheets and dialogs to HomeActivity
    fun onClickOnBoardAgent()
    fun onClickOnBoardMerchant()
    fun onClickOnBoardCustomers()
    fun onClickViewBalance(viewWalletScope: String, data: WalletData?)
}