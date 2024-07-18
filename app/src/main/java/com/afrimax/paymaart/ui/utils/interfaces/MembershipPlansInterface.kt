package com.afrimax.paymaart.ui.utils.interfaces

import com.afrimax.paymaart.ui.membership.MembershipPlanModel

interface MembershipPlansInterface {
    fun onSubmitClicked(membershipPlanModel: MembershipPlanModel)
    //For auto-renewal.
    fun onConfirm()
    fun onCancelClicked()
}