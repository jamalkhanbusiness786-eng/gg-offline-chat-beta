package com.example.data.model

import java.util.UUID

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    FAILED
}

data class ChatMessage(
    val messageId: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val senderName: String,
    val senderAddress: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val isOutgoing: Boolean = true
)
