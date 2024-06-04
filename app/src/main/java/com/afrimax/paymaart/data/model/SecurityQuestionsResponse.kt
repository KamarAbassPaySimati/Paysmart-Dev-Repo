package com.afrimax.paymaart.data.model

data class SecurityQuestionsResponse(
    val success_status: Boolean,
    val data: List<SecurityQuestionModel>,
    val message: String,
)

data class SecurityQuestionAnswerModel(
    val question_id: String,
    val answer: String,
)

data class SecurityQuestionModel(
    val id: String, val question: String
)