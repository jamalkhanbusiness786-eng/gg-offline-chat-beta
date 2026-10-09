package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth.GG_CHAT_UUID
import com.example.data.model.BluetoothDevicePeer
import com.example.data.model.ChatMessage
import com.example.data.model.MessageStatus
import com.example.data.model.PRESET_AVATARS
import com.example.ui.theme.GGBorder
import com.example.ui.theme.GGDarkBackground
import com.example.ui.theme.GGDarkSurface
import com.example.ui.theme.GGDarkSurfaceVariant
import com.example.ui.theme.GGElectricGreen
import com.example.ui.theme.GGElectricGreenContainer
import com.example.ui.theme.GGElectricGreenNeon
import com.example.ui.theme.GGOnGreen
import com.example.ui.theme.GGReceivedBubble
import com.example.ui.theme.GGSentBubble
import com.example.ui.theme.GGStatusConnected
import com.example.ui.theme.GGStatusError
import com.example.ui.theme.GGTextMuted
import com.example.ui.theme.GGTextPrimary
import com.example.ui.theme.GGTextSecondary
import com.example.ui.viewmodel.GGViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    peer: BluetoothDevicePeer,
    viewModel: GGViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.getMessagesForConversation(peer.address).collectAsState()
    val bluetoothState by viewModel.bluetoothState.collectAsState()

    val isConnected = bluetoothState.connectedPeer?.address == peer.address
    var inputText by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GGDarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Chat Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GGDarkSurface)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("chat_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = GGTextPrimary
                )
            }

            // Peer Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GGElectricGreenContainer)
                    .border(1.dp, GGElectricGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val avatar = PRESET_AVATARS.getOrElse(peer.avatarId) { "⚡" }
                Text(text = avatar, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = peer.name.ifBlank { "Nearby Peer" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GGTextPrimary,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) GGStatusConnected else Color(0xFF757575))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isConnected) "Connected (Bluetooth RFCOMM)" else "Offline / Out of Range",
                        fontSize = 11.sp,
                        color = if (isConnected) GGElectricGreen else GGTextMuted
                    )
                }
            }

            IconButton(onClick = { showInfoDialog = true }) {
                Icon(Icons.Default.Info, contentDescription = "Peer Info", tint = GGTextSecondary)
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = GGTextSecondary)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(GGDarkSurfaceVariant)
                ) {
                    DropdownMenuItem(
                        text = { Text("Clear Conversation", color = Color(0xFFFF5252)) },
                        onClick = {
                            menuExpanded = false
                            showClearDialog = true
                        }
                    )
                }
            }
        }

        // Connection Disconnected Warning Banner if not connected
        if (!isConnected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF261C14))
                    .border(0.5.dp, Color(0xFF8A5A2B), RoundedCornerShape(0.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bluetooth socket disconnected. Messages will queue and can be retried upon reconnect.",
                        fontSize = 11.sp,
                        color = Color(0xFFFFE082),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Messages List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(GGElectricGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📡", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Direct Bluetooth Session",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GGTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Say hello! Packets travel directly over Bluetooth Classic RFCOMM. Delivered checks only appear when peer acknowledges receipt.",
                        fontSize = 13.sp,
                        color = GGTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages, key = { it.messageId }) { msg ->
                        MessageBubble(
                            message = msg,
                            onRetry = { viewModel.retryMessage(msg) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Bottom Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GGDarkSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Offline message...", color = GGTextMuted) },
                singleLine = false,
                maxLines = 4,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(peer.address, inputText)
                        inputText = ""
                    }
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GGDarkSurfaceVariant,
                    unfocusedContainerColor = GGDarkSurfaceVariant,
                    focusedBorderColor = GGElectricGreen,
                    unfocusedBorderColor = GGBorder,
                    focusedTextColor = GGTextPrimary,
                    unfocusedTextColor = GGTextPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) GGElectricGreen else GGBorder)
                    .clickable(enabled = inputText.isNotBlank()) {
                        viewModel.sendMessage(peer.address, inputText)
                        inputText = ""
                    }
                    .testTag("send_message_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank()) GGOnGreen else GGTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text(text = "Connection Details", color = GGTextPrimary) },
            text = {
                Column {
                    Text(text = "Peer Name: ${peer.name}", color = GGTextSecondary, fontSize = 13.sp)
                    Text(text = "Device Address: ${peer.address}", color = GGTextSecondary, fontSize = 13.sp)
                    if (!peer.maskedPhone.isNullOrBlank()) {
                        Text(text = "Masked ID: ${peer.maskedPhone}", color = GGTextSecondary, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Transport: Bluetooth Classic RFCOMM Sockets (UUID: $GG_CHAT_UUID). Packets are exchanged peer-to-peer without servers or internet relay.",
                        color = GGTextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("OK", color = GGElectricGreen)
                }
            },
            containerColor = GGDarkSurfaceVariant
        )
    }

    // Clear Conversation Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = "Clear Conversation?", color = GGTextPrimary) },
            text = {
                Text(
                    text = "This will permanently delete all messages with ${peer.name} from this device.",
                    color = GGTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showClearDialog = false
                    viewModel.clearConversation(peer.address)
                    onBack()
                }) {
                    Text("Delete", color = Color(0xFFFF5252))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = GGTextSecondary)
                }
            },
            containerColor = GGDarkSurfaceVariant
        )
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onRetry: () -> Unit
) {
    val isOutgoing = message.isOutgoing
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isOutgoing) 16.dp else 4.dp,
                            bottomEnd = if (isOutgoing) 4.dp else 16.dp
                        )
                    )
                    .background(if (isOutgoing) GGSentBubble else GGReceivedBubble)
                    .border(
                        1.dp,
                        if (isOutgoing) GGElectricGreen.copy(alpha = 0.35f) else GGBorder,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isOutgoing) 16.dp else 4.dp,
                            bottomEnd = if (isOutgoing) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    if (!isOutgoing) {
                        Text(
                            text = message.senderName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GGElectricGreenNeon
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Text(
                        text = message.text,
                        fontSize = 15.sp,
                        color = GGTextPrimary,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Timestamp & Delivery status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = formattedTime,
                    fontSize = 10.sp,
                    color = GGTextMuted
                )

                if (isOutgoing) {
                    Spacer(modifier = Modifier.width(4.dp))
                    when (message.status) {
                        MessageStatus.SENDING -> {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Sending",
                                tint = GGTextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        MessageStatus.SENT -> {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Sent",
                                tint = GGTextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        MessageStatus.DELIVERED -> {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Delivered (ACK)",
                                tint = GGElectricGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        MessageStatus.FAILED -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onRetry() }
                                    .padding(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Failed",
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Retry",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF5252)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
