package com.afrimax.paymaart.ui.utils.interfaces

interface KycYourPersonalDetailsInterface {
    fun onEmailVerified(recordId:String)
    fun onPhoneVerified(recordId:String)
}