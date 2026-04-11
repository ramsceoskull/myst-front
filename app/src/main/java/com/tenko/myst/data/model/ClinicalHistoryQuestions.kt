package com.tenko.myst.data.model

val clinicalHistoryQuestions = listOf(
    Question(
        id = "diabetes_mellitus",
        text = "¿Tiene usted diabetes?",
        type = QuestionType.SingleChoice(
            mapOf(
                "none" to "Ninguna",
                "type_1" to "Tipo 1",
                "type_2" to "Tipo 2",
                "gestational" to "Gestacional",
                "prediabetes" to "Prediabetes"
            )
        )
    ),
    Question(
        id = "arterial_hypertension",
        text = "¿Padece hipertensión arterial?",
        type = QuestionType.BooleanChoice
    ),
    Question(
        id = "miscarriages_abortions",
        text = "Número de abortos:",
        type = QuestionType.NumericInput
    )
)
