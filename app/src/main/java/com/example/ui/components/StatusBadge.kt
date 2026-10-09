package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth.GlobalBluetoothStatus
import com.example.ui.theme.GGElectricGreen
import com.example.ui.theme.GGStatusConnected
import com.example.ui.theme.GGStatusError
import com.example.ui.theme.GGStatusOffline
import com.example.ui.theme.GGStatusPending
import com.example.ui.theme.GGStatusSearching

@Composable
fun StatusBadge(
    status: GlobalBluetoothStatus,
    connectedPeerName: String? = null,
    modifier: Modifier = Modifier
) {
    val (dotColor, text, containerBg) = when (status) {
        GlobalBluetoothStatus.CONNECTED_TO_NEARBY -> {
            val label = if (connectedPeerName != null) "Connected: $connectedPeerName" else "Connected Nearby"
            Triple(GGStatusConnected, label, Color(0x2200E676))
        }
        GlobalBluetoothStatus.SEARCHING_NEARBY ->
            Triple(GGStatusSearching, "Searching Nearby…", Color(0x2200B0FF))
        GlobalBluetoothStatus.BLUETOOTH_OFF ->
            Triple(GGStatusError, "Bluetooth Off", Color(0x22FF5252))
        GlobalBluetoothStatus.MESSAGE_PENDING ->
            Triple(GGStatusPending, "Message Pending", Color(0x22FFB300))
        GlobalBluetoothStatus.OFFLINE ->
            Triple(GGStatusOffline, "Offline", Color(0x22757575))
    }

    val animatedColor by animateColorAsState(targetValue = dotColor, label = "statusColor")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("status_badge")
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg)
            .border(1.dp, animatedColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(animatedColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
