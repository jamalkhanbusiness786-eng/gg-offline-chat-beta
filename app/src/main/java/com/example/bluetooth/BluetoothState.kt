package com.example.bluetooth

import com.example.data.model.BluetoothDevicePeer

enum class GlobalBluetoothStatus {
    BLUETOOTH_OFF,
    SEARCHING_NEARBY,
    CONNECTED_TO_NEARBY,
    OFFLINE,
    MESSAGE_PENDING
}

data class ConnectionRequest(
    val peerAddress: String,
    val peerName: String,
    val peerMaskedPhone: String,
    val peerAvatarId: Int,
    val isVirtualSandbox: Boolean = false
)

data class BluetoothUiState(
    val isBluetoothSupported: Boolean = true,
    val isBluetoothEnabled: Boolean = false,
    val isDiscovering: Boolean = false,
    val globalStatus: GlobalBluetoothStatus = GlobalBluetoothStatus.OFFLINE,
    val connectedPeer: BluetoothDevicePeer? = null,
    val discoveredPeers: List<BluetoothDevicePeer> = emptyList(),
    val incomingConnectionRequest: ConnectionRequest? = null,
    val isVirtualSandboxEnabled: Boolean = false,
    val lastError: String? = null
)
