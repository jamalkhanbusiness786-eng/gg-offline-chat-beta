package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.ChatMessage
import com.example.data.model.MessageStatus

@Entity(
    tableName = "chat_messages",
    indices = [Index(value = ["conversationId"]), Index(value = ["timestamp"])]
)
data class ChatMessageEntity(
    @PrimaryKey val messageId: String,
    val conversationId: String,
    val senderName: String,
    val senderAddress: String,
    val text: String,
    val timestamp: Long,
    val status: String,
    val isOutgoing: Boolean
) {
    fun toModel(): ChatMessage = ChatMessage(
        messageId = messageId,
        conversationId = conversationId,
        senderName = senderName,
        senderAddress = senderAddress,
        text = text,
        timestamp = timestamp,
        status = try { MessageStatus.valueOf(status) } catch (e: Exception) { MessageStatus.SENT },
        isOutgoing = isOutgoing
    )

    companion object {
        fun fromModel(model: ChatMessage): ChatMessageEntity = ChatMessageEntity(
            messageId = model.messageId,
            conversationId = model.conversationId,
            senderName = model.senderName,
            senderAddress = model.senderAddress,
            text = model.text,
            timestamp = model.timestamp,
            status = model.status.name,
            isOutgoing = model.isOutgoing
        )
    }
}
