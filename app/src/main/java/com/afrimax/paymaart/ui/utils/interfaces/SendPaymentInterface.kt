package com.afrimax.paymaart.ui.utils.interfaces

import com.afrimax.paymaart.ui.membership.MembershipPlanModel

interface SendPaymentInterface {
    fun onPaymentSuccess(successData: Any?)
    fun onPaymentFailure(message: String)

    //MembershipPlansPurchaseBottomSheetOnRenewal
    fun onSubmitClicked(membershipPlanModel: MembershipPlanModel)
}