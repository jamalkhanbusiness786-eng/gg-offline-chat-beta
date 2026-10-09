package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetooth.GG_CHAT_UUID
import com.example.data.model.BluetoothDevicePeer
import com.example.data.model.Conversation
import com.example.data.model.PRESET_AVATARS
import com.example.data.model.PeerConnectionState
import com.example.data.model.UserProfile
import com.example.ui.components.StatusBadge
import com.example.ui.components.rememberBluetoothPermissionLauncher
import com.example.ui.theme.GGBorder
import com.example.ui.theme.GGBorderLight
import com.example.ui.theme.GGDarkBackground
import com.example.ui.theme.GGDarkSurface
import com.example.ui.theme.GGDarkSurfaceHighlight
import com.example.ui.theme.GGDarkSurfaceVariant
import com.example.ui.theme.GGElectricGreen
import com.example.ui.theme.GGElectricGreenContainer
import com.example.ui.theme.GGElectricGreenNeon
import com.example.ui.theme.GGOnGreen
import com.example.ui.theme.GGStatusConnected
import com.example.ui.theme.GGTextMuted
import com.example.ui.theme.GGTextPrimary
import com.example.ui.theme.GGTextSecondary
import com.example.ui.viewmodel.GGViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: GGViewModel,
    onOpenChat: (BluetoothDevicePeer) -> Unit,
    onEditProfile: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val profile by viewModel.profileFlow.collectAsState()
    val bluetoothState by viewModel.bluetoothState.collectAsState()

    val requestPermissions = rememberBluetoothPermissionLauncher { granted ->
        if (granted) {
            viewModel.bluetoothController.refreshBondedDevices()
        }
    }

    Scaffold(
        topBar = {
            HomeTopHeader(
                userProfile = profile,
                bluetoothState = bluetoothState,
                onAvatarClick = { selectedTab = 3 }
            )
        },
        bottomBar = {
            HomeBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = GGDarkBackground,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> ChatsTab(
                    viewModel = viewModel,
                    onOpenChat = onOpenChat,
                    onNavigateToNearby = { selectedTab = 1 }
                )
                1 -> NearbyTab(
                    viewModel = viewModel,
                    onRequestPermissions = requestPermissions,
                    onOpenChat = onOpenChat
                )
                2 -> GlobalTab()
                3 -> ProfileTab(
                    userProfile = profile,
                    viewModel = viewModel,
                    onEditProfile = onEditProfile
                )
            }
        }
    }
}

@Composable
fun HomeTopHeader(
    userProfile: UserProfile,
    bluetoothState: com.example.bluetooth.BluetoothUiState,
    onAvatarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GGDarkSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // GG Logo & App Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GGElectricGreenContainer)
                        .border(1.5.dp, GGElectricGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GG",
                        color = GGElectricGreenNeon,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GG Offline Chat",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GGTextPrimary
                    )
                    Text(
                        text = userProfile.displayName.ifBlank { "Offline User" },
                        fontSize = 12.sp,
                        color = GGTextSecondary
                    )
                }
            }

            // User Avatar Clickable to Profile
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(GGDarkSurfaceVariant)
                    .border(1.dp, GGBorder, CircleShape)
                    .clickable { onAvatarClick() }
                    .testTag("top_bar_user_avatar"),
                contentAlignment = Alignment.Center
            ) {
                val avatarChar = PRESET_AVATARS.getOrElse(userProfile.avatarId) { "⚡" }
                Text(text = avatarChar, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Connection Status Indicator Badge
        StatusBadge(
            status = bluetoothState.globalStatus,
            connectedPeerName = bluetoothState.connectedPeer?.name
        )
    }
}

@Composable
fun HomeBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = GGDarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("home_bottom_navigation")
    ) {
        val navItems = listOf(
            Triple("Chats", Icons.AutoMirrored.Filled.Chat, 0),
            Triple("Nearby", Icons.Default.NearMe, 1),
            Triple("Global", Icons.Default.Language, 2),
            Triple("Profile", Icons.Default.Person, 3)
        )

        navItems.forEach { (label, icon, index) ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GGOnGreen,
                    selectedTextColor = GGElectricGreen,
                    indicatorColor = GGElectricGreen,
                    unselectedIconColor = GGTextMuted,
                    unselectedTextColor = GGTextMuted
                )
            )
        }
    }
}

// ==========================================
// TAB 1: CHATS TAB
// ==========================================
@Composable
fun ChatsTab(
    viewModel: GGViewModel,
    onOpenChat: (BluetoothDevicePeer) -> Unit,
    onNavigateToNearby: () -> Unit
) {
    val conversations by viewModel.conversationsFlow.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Search Conversations Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search conversations...", color = GGTextMuted, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = GGElectricGreen)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = GGTextMuted)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_conversations_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GGDarkSurface,
                unfocusedContainerColor = GGDarkSurface,
                focusedBorderColor = GGElectricGreen,
                unfocusedBorderColor = GGBorder,
                focusedTextColor = GGTextPrimary,
                unfocusedTextColor = GGTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (conversations.isEmpty()) {
            // Empty State Guidance
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GGElectricGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BluetoothSearching,
                        contentDescription = null,
                        tint = GGElectricGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = if (searchQuery.isNotBlank()) "No matching conversations" else "No Conversations Yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GGTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (searchQuery.isNotBlank())
                        "Try a different search term or check all chats."
                    else
                        "Find and connect with nearby GG users via Bluetooth to start an offline chat.",
                    fontSize = 14.sp,
                    color = GGTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                if (searchQuery.isBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToNearby,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GGElectricGreen,
                            contentColor = GGOnGreen
                        ),
                        modifier = Modifier.testTag("find_nearby_cta_button")
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FIND NEARBY USERS", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(conversations, key = { it.conversationId }) { conv ->
                    ConversationItemRow(
                        conversation = conv,
                        onClick = {
                            val peer = BluetoothDevicePeer(
                                address = conv.deviceAddress,
                                name = conv.peerName,
                                maskedPhone = conv.peerMaskedPhone,
                                avatarId = conv.peerAvatarId
                            )
                            onOpenChat(peer)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationItemRow(
    conversation: Conversation,
    onClick: () -> Unit
) {
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(conversation.lastMessageTimestamp) {
        timeFormatter.format(Date(conversation.lastMessageTimestamp))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GGDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GGBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("conversation_row_${conversation.conversationId}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GGElectricGreenContainer)
                    .border(1.dp, if (conversation.isOnlineNearby) GGElectricGreen else GGBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val avatarChar = PRESET_AVATARS.getOrElse(conversation.peerAvatarId) { "⚡" }
                Text(text = avatarChar, fontSize = 22.sp)

                if (conversation.isOnlineNearby) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GGElectricGreen)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.peerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GGTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 11.sp,
                        color = GGTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.lastMessageText.ifBlank { "Tap to send offline message" },
                        fontSize = 13.sp,
                        color = if (conversation.unreadCount > 0) GGTextPrimary else GGTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (conversation.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(GGElectricGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conversation.unreadCount.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GGOnGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: NEARBY TAB
// ==========================================
@Composable
fun NearbyTab(
    viewModel: GGViewModel,
    onRequestPermissions: () -> Unit,
    onOpenChat: (BluetoothDevicePeer) -> Unit
) {
    val bluetoothState by viewModel.bluetoothState.collectAsState()
    val hasPermissions = viewModel.bluetoothController.hasPermissions()
    val profile by viewModel.profileFlow.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "nearbyPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Permissions Card if missing
        if (!hasPermissions) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF261D13)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8A5A2B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFB300))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bluetooth Permissions Required",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GGTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "To discover other GG phones and exchange messages offline, Android requires nearby device permissions (BLUETOOTH_SCAN & BLUETOOTH_CONNECT).",
                            fontSize = 12.sp,
                            color = GGTextSecondary,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onRequestPermissions,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300),
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("grant_permissions_button")
                        ) {
                            Text("GRANT PERMISSIONS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Discovery Controls Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GGDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GGBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bluetoothState.isDiscovering) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = GGElectricGreen.copy(alpha = 0.25f),
                                    radius = size.minDimension / 2 * pulseRadius,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(if (bluetoothState.isDiscovering) GGElectricGreenContainer else GGDarkSurfaceHighlight)
                                .border(1.5.dp, if (bluetoothState.isDiscovering) GGElectricGreen else GGBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (bluetoothState.isDiscovering) Icons.Default.Sensors else Icons.Default.Bluetooth,
                                contentDescription = null,
                                tint = if (bluetoothState.isDiscovering) GGElectricGreenNeon else GGTextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (bluetoothState.isDiscovering) "Scanning for Nearby GG Devices…" else "Bluetooth Discovery Idle",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GGTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Both devices must have Bluetooth turned on and be within ~30m range.",
                        fontSize = 12.sp,
                        color = GGTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (bluetoothState.isDiscovering) {
                            OutlinedButton(
                                onClick = { viewModel.bluetoothController.stopDiscovery() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("stop_discovery_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252))
                            ) {
                                Text("STOP DISCOVERY")
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (hasPermissions) {
                                        viewModel.bluetoothController.startDiscovery()
                                    } else {
                                        onRequestPermissions()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("start_discovery_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GGElectricGreen,
                                    contentColor = GGOnGreen
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("START DISCOVERY", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Emulator / Virtual Sandbox Testing Mode Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GGDarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, GGBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Virtual Peer Sandbox (Emulator Testing)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GGElectricGreen
                            )
                            Text(
                                text = "Enables virtual peers to test connection approval, message transit & delivery ACKs in browser emulator.",
                                fontSize = 11.sp,
                                color = GGTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                        Switch(
                            checked = bluetoothState.isVirtualSandboxEnabled,
                            onCheckedChange = { viewModel.bluetoothController.toggleVirtualSandbox(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GGOnGreen,
                                checkedTrackColor = GGElectricGreen
                            ),
                            modifier = Modifier.testTag("virtual_sandbox_toggle")
                        )
                    }

                    if (bluetoothState.isVirtualSandboxEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.bluetoothController.triggerSimulatedIncomingRequest() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("trigger_incoming_request_test_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Simulate Incoming Request from Nearby Node", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Discovered Devices Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AVAILABLE PEERS (${bluetoothState.discoveredPeers.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GGTextMuted,
                    letterSpacing = 1.sp
                )
            }
        }

        if (bluetoothState.discoveredPeers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (bluetoothState.isDiscovering)
                            "Scanning RFCOMM signals nearby…"
                        else
                            "No devices found. Tap 'START DISCOVERY' or toggle Virtual Sandbox for emulator tests.",
                        fontSize = 13.sp,
                        color = GGTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(bluetoothState.discoveredPeers, key = { it.address }) { peer ->
                val isConnectedWithThis = bluetoothState.connectedPeer?.address == peer.address
                val isConnectingThis = peer.connectionState == PeerConnectionState.CONNECTING

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isConnectedWithThis) GGElectricGreenContainer else GGDarkSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isConnectedWithThis) GGElectricGreen else GGBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GGDarkSurfaceHighlight),
                            contentAlignment = Alignment.Center
                        ) {
                            val avatarChar = PRESET_AVATARS.getOrElse(peer.avatarId) { "⚡" }
                            Text(text = avatarChar, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = peer.displayNameClean,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GGTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (peer.rssi != null) {
                                    Icon(
                                        imageVector = Icons.Default.SignalCellularAlt,
                                        contentDescription = null,
                                        tint = GGElectricGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${peer.rssi} dBm • ",
                                        fontSize = 11.sp,
                                        color = GGTextSecondary
                                    )
                                }
                                Text(
                                    text = if (peer.isVirtualSandbox) "Sandbox Peer" else peer.address,
                                    fontSize = 11.sp,
                                    color = GGTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isConnectedWithThis) {
                            Button(
                                onClick = { onOpenChat(peer) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GGElectricGreen,
                                    contentColor = GGOnGreen
                                ),
                                modifier = Modifier.testTag("open_chat_button_${peer.address}")
                            ) {
                                Text("CHAT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else if (isConnectingThis) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = GGElectricGreen,
                                strokeWidth = 2.dp
                            )
                        } else {
                            OutlinedButton(
                                onClick = {
                                    viewModel.bluetoothController.connectToPeer(peer, profile)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = GGElectricGreen
                                ),
                                modifier = Modifier.testTag("connect_peer_button_${peer.address}")
                            ) {
                                Text("CONNECT", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: GLOBAL TAB
// ==========================================
@Composable
fun GlobalTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GGDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GGBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GGElectricGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = GGElectricGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Global Community",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GGTextPrimary
                            )
                            Text(
                                text = "Offline Limitations & Transparency",
                                fontSize = 12.sp,
                                color = GGElectricGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Honest Note: Native Bluetooth radio waves propagate directly peer-to-peer up to ~30-50 meters line-of-sight. Bluetooth alone cannot deliver messages across cities, states, or countries without an active internet connection or a multi-hop mesh relay network.",
                        fontSize = 13.sp,
                        color = GGTextSecondary,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "GG Beta 0.1 is engineered purely for true offline local survival & nearby direct communication. We will NEVER fake online status or claim to contact worldwide users over Bluetooth.",
                        fontSize = 12.sp,
                        color = GGTextMuted,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "COMMUNITY BULLETIN & OFFLINE PROTOCOL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GGTextMuted,
                letterSpacing = 1.sp
            )
        }

        item {
            CommunityCard(
                tag = "ROADMAP",
                title = "Multi-Hop Mesh Relay Architecture",
                description = "In upcoming versions, GG devices will act as store-and-forward relay nodes. A message will be able to hop across multiple intermediate phones to reach someone 200m away, even with zero internet."
            )
        }

        item {
            CommunityCard(
                tag = "OFFLINE TIP",
                title = "Maximizing Bluetooth Range",
                description = "Keep phones in line-of-sight without dense concrete or metal shielding. Outdoors in open fields, Bluetooth Classic RFCOMM can reach up to 40-60 meters cleanly."
            )
        }

        item {
            CommunityCard(
                tag = "PRIVACY",
                title = "Zero Identity Tracking",
                description = "Since there are no central servers, your profile, device MAC address, and chat logs are stored strictly on your local flash memory. No ads, no analytics, no corporate logging."
            )
        }
    }
}

@Composable
fun CommunityCard(
    tag: String,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GGDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, GGBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(GGElectricGreenContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = tag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GGElectricGreenNeon
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = GGTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = GGTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

// ==========================================
// TAB 4: PROFILE & SETTINGS TAB
// ==========================================
@Composable
fun ProfileTab(
    userProfile: UserProfile,
    viewModel: GGViewModel,
    onEditProfile: () -> Unit
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showClearHistoryConfirmDialog by remember { mutableStateOf(false) }

    val creationDateFormatted = remember(userProfile.createdAt) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(userProfile.createdAt))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GGDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GGBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(GGElectricGreenContainer)
                            .border(2.dp, GGElectricGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val avatar = PRESET_AVATARS.getOrElse(userProfile.avatarId) { "⚡" }
                        Text(text = avatar, fontSize = 34.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = userProfile.displayName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GGTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Masked ID: ${userProfile.maskedPhoneNumber}",
                        fontSize = 13.sp,
                        color = GGElectricGreen
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Created on $creationDateFormatted • Beta 0.1",
                        fontSize = 11.sp,
                        color = GGTextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onEditProfile,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GGDarkSurfaceHighlight,
                            contentColor = GGTextPrimary
                        ),
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Profile Details", fontSize = 13.sp)
                    }
                }
            }
        }

        // Bluetooth Transport Diagnostics
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GGDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GGBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "BLUETOOTH HARDWARE DIAGNOSTICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GGTextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Protocol: RFCOMM Serial Port Profile (SPP)",
                        fontSize = 13.sp,
                        color = GGTextSecondary
                    )
                    Text(
                        text = "Service UUID: $GG_CHAT_UUID",
                        fontSize = 11.sp,
                        color = GGTextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Zero OTP / Passwords: Peer authentication is verified locally at connection time via handshake exchange.",
                        fontSize = 12.sp,
                        color = GGTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Data & Privacy Actions
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GGDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GGBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LOCAL DATA & STORAGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GGTextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearHistoryConfirmDialog = true }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = GGTextSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Clear Chat History", fontSize = 14.sp, color = GGTextPrimary)
                            Text("Deletes all local message logs while keeping profile", fontSize = 11.sp, color = GGTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteConfirmDialog = true }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFFF5252))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Delete Profile & Reset App", fontSize = 14.sp, color = Color(0xFFFF5252))
                            Text("Permanently erases profile and all local data", fontSize = 11.sp, color = GGTextMuted)
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialogs
    if (showClearHistoryConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryConfirmDialog = false },
            title = { Text("Clear All Chat History?", color = GGTextPrimary) },
            text = {
                Text(
                    "This will delete all conversations and messages stored on this device.",
                    color = GGTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showClearHistoryConfirmDialog = false
                    // Delete conversations & messages
                    viewModel.deleteEntireAccountAndHistory()
                    // Restore profile
                    viewModel.saveProfile(userProfile)
                }) {
                    Text("Clear All", color = Color(0xFFFF5252))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryConfirmDialog = false }) {
                    Text("Cancel", color = GGTextSecondary)
                }
            },
            containerColor = GGDarkSurfaceVariant
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Profile & Reset?", color = GGTextPrimary) },
            text = {
                Text(
                    "This will permanently erase your profile and all offline chat data from this device. You will return to the welcome screen.",
                    color = GGTextSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmDialog = false
                    viewModel.deleteEntireAccountAndHistory()
                }) {
                    Text("Delete Everything", color = Color(0xFFFF5252))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = GGTextSecondary)
                }
            },
            containerColor = GGDarkSurfaceVariant
        )
    }
}
