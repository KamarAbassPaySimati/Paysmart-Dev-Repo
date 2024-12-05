package com.afrimax.paysimati.data.model

import com.google.gson.annotations.SerializedName

data class VerifyForgotOtpResponse(
    val message: String, val question: ArrayList<Question>
) {
    data class Question(
        @SerializedName("question_id") val questionId: String,
        @SerializedName("question") val question: String,
    )
}
