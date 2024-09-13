package com.afrimax.paymaart.ui.utils.interfaces

import com.afrimax.paymaart.ui.home.MembershipType
import com.afrimax.paymaart.ui.membership.MembershipPlanModel

interface MembershipPlansInterface {
    fun onSubmitClicked(membershipPlanModel: MembershipPlanModel)
    //For auto-renewal.
    fun onConfirm(membershipType: MembershipType)
    fun onCancelClicked(membershipType: MembershipType)
}