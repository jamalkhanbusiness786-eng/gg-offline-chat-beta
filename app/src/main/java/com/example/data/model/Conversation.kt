package com.example.data.model

data class Conversation(
    val conversationId: String, // Typically the peer's Bluetooth MAC address or unique peer ID
    val peerName: String,
    val peerMaskedPhone: String,
    val peerAvatarId: Int = 0,
    val deviceAddress: String,
    val lastMessageText: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isOnlineNearby: Boolean = false
)
