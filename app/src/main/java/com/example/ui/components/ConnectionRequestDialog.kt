package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bluetooth.ConnectionRequest
import com.example.data.model.PRESET_AVATARS
import com.example.ui.theme.GGElectricGreen
import com.example.ui.theme.GGElectricGreenContainer
import com.example.ui.theme.GGOnGreen
import com.example.ui.theme.GGTextMuted
import com.example.ui.theme.GGTextPrimary
import com.example.ui.theme.GGTextSecondary

@Composable
fun ConnectionRequestDialog(
    request: ConnectionRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Dialog(onDismissRequest = onDecline) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF141D18)
            ),
            modifier = Modifier
                .testTag("connection_request_dialog")
                .fillMaxWidth()
                .border(1.5.dp, GGElectricGreen.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(GGElectricGreenContainer)
                        .border(1.5.dp, GGElectricGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val avatarChar = PRESET_AVATARS.getOrElse(request.peerAvatarId) { "⚡" }
                    Text(text = avatarChar, fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Connection Request",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GGTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${request.peerName} wants to establish a direct offline Bluetooth chat session with your device.",
                    fontSize = 14.sp,
                    color = GGTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )

                if (request.peerMaskedPhone.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Masked ID: ${request.peerMaskedPhone}",
                        fontSize = 12.sp,
                        color = GGTextMuted
                    )
                }

                if (request.isVirtualSandbox) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Virtual Peer Sandbox Test •",
                        fontSize = 11.sp,
                        color = GGElectricGreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("decline_connection_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF5252)
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Decline")
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("accept_connection_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GGElectricGreen,
                            contentColor = GGOnGreen
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Accept", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
