package com.tenko.myst.data.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenko.myst.data.model.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis(),
            text = text,
            isUser = true
        )

        _messages.value += userMessage

        simulateAssistantResponse()
    }

    private fun simulateAssistantResponse() {
        viewModelScope.launch {

            _isTyping.value = true
            delay(1500)

            val assistantMessage = ChatMessage(
                id = System.currentTimeMillis(),
                text = generateResponse(),
                isUser = false
            )

            _messages.value += assistantMessage
            _isTyping.value = false
        }
    }

    private fun generateResponse(): String {
        return listOf(
            "Gracias por compartirlo 💕",
            "Estoy aquí contigo.",
            "Cuéntame un poco más.",
            "Eso es muy importante."
        ).random()
    }
}