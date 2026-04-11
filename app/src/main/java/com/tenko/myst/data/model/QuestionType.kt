package com.tenko.myst.data.model

sealed class QuestionType {
    data class SingleChoice(val options: Map<String, String>) : QuestionType()
    object BooleanChoice : QuestionType()
    object NumericInput : QuestionType()
}