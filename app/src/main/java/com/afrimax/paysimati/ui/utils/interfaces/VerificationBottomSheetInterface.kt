package com.afrimax.paysimati.ui.utils.interfaces

interface VerificationBottomSheetInterface {
    fun onEmailVerified(recordId:String)
    fun onPhoneVerified(recordId:String)
}