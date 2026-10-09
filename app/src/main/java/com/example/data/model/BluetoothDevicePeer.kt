package com.example.data.model

enum class PeerConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    PAIRING_REQUIRED,
    REQUESTING_APPROVAL
}

data class BluetoothDevicePeer(
    val address: String,
    val name: String,
    val rssi: Int? = null,
    val isBonded: Boolean = false,
    val connectionState: PeerConnectionState = PeerConnectionState.DISCONNECTED,
    val isGGCompatible: Boolean = true,
    val maskedPhone: String? = null,
    val avatarId: Int = 0,
    val isVirtualSandbox: Boolean = false
) {
    val displayNameClean: String
        get() = name.ifBlank { "Nearby Bluetooth Device (${address.takeLast(5)})" }
}
