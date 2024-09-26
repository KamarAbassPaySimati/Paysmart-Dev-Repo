package com.afrimax.paysimati.ui.utils.interfaces

import com.afrimax.paysimati.ui.home.MembershipType
import com.afrimax.paysimati.ui.membership.MembershipPlanModel

interface MembershipPlansInterface {
    fun onSubmitClicked(membershipPlanModel: MembershipPlanModel)
    //For auto-renewal.
    fun onConfirm(membershipType: MembershipType)
    fun onCancelClicked(membershipType: MembershipType)
}