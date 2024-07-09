package com.afrimax.paymaart.ui.utils.interfaces

import com.afrimax.paymaart.ui.home.MembershipType

interface MembershipPlansInterface {
    fun onSubmitClicked(membershipValidityType: String, autoRenewal: Boolean, membershipType: String)
}