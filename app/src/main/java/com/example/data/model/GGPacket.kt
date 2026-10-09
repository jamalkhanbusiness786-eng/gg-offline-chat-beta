package com.example.data.model

import org.json.JSONObject

enum class PacketType {
    HANDSHAKE,
    HANDSHAKE_ACK,
    CHAT_MSG,
    MSG_ACK,
    DISCONNECT,
    PING
}

data class GGPacket(
    val type: PacketType,
    val packetId: String,
    val senderName: String,
    val senderMaskedPhone: String,
    val senderAvatar: Int,
    val payload: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toJsonString(): String {
        val json = JSONObject()
        json.put("type", type.name)
        json.put("packetId", packetId)
        json.put("senderName", senderName)
        json.put("senderMaskedPhone", senderMaskedPhone)
        json.put("senderAvatar", senderAvatar)
        json.put("payload", payload)
        json.put("timestamp", timestamp)
        return json.toString()
    }

    companion object {
        fun fromJsonString(jsonStr: String): GGPacket? {
            return try {
                val json = JSONObject(jsonStr)
                GGPacket(
                    type = PacketType.valueOf(json.getString("type")),
                    packetId = json.getString("packetId"),
                    senderName = json.optString("senderName", "Nearby Peer"),
                    senderMaskedPhone = json.optString("senderMaskedPhone", ""),
                    senderAvatar = json.optInt("senderAvatar", 0),
                    payload = json.optString("payload", ""),
                    timestamp = json.optLong("timestamp", System.currentTimeMillis())
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
