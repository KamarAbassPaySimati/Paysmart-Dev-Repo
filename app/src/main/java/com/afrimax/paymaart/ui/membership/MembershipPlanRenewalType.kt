package com.afrimax.paymaart.ui.membership

data class MembershipPlanRenewalType (
    val validityDay: String?,
    val primePrice: String?,
    val primeXPrice: String?,
    val referenceNumber: String,
    val validDate: String = getValidDate(validityDay)
){
    companion object{
        fun getValidDate(planString: String?): String {
            if (planString.isNullOrEmpty())
                return ""
            val regex = Regex("""Prepaid, (\d+) Days, MWK""")
            val matchResult = regex.find(planString)
            return matchResult?.groupValues?.getOrNull(1) ?: ""

        }
    }
}
