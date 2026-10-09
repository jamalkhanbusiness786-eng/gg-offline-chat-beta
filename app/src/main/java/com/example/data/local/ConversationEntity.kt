package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Conversation

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val conversationId: String,
    val peerName: String,
    val peerMaskedPhone: String,
    val peerAvatarId: Int,
    val deviceAddress: String,
    val lastMessageText: String,
    val lastMessageTimestamp: Long,
    val unreadCount: Int = 0
) {
    fun toModel(isOnlineNearby: Boolean = false): Conversation = Conversation(
        conversationId = conversationId,
        peerName = peerName,
        peerMaskedPhone = peerMaskedPhone,
        peerAvatarId = peerAvatarId,
        deviceAddress = deviceAddress,
        lastMessageText = lastMessageText,
        lastMessageTimestamp = lastMessageTimestamp,
        unreadCount = unreadCount,
        isOnlineNearby = isOnlineNearby
    )

    companion object {
        fun fromModel(model: Conversation): ConversationEntity = ConversationEntity(
            conversationId = model.conversationId,
            peerName = model.peerName,
            peerMaskedPhone = model.peerMaskedPhone,
            peerAvatarId = model.peerAvatarId,
            deviceAddress = model.deviceAddress,
            lastMessageText = model.lastMessageText,
            lastMessageTimestamp = model.lastMessageTimestamp,
            unreadCount = model.unreadCount
        )
    }
}
