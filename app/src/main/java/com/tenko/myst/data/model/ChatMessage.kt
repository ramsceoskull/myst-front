package com.tenko.myst.data.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val questionRef: ClinicalQuestion? = null
)