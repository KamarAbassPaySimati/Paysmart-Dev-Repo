package com.afrimax.paysimati.ui.utils.interfaces

import com.afrimax.paysimati.ui.membership.MembershipPlanModel

interface SendPaymentInterface {
    fun onPaymentSuccess(successData: Any?)
    fun onPaymentFailure(message: String)

    //MembershipPlansPurchaseBottomSheetOnRenewal
    fun onSubmitClicked(membershipPlanModel: MembershipPlanModel)
}