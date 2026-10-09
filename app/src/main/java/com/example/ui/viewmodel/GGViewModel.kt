package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth.BluetoothController
import com.example.bluetooth.BluetoothUiState
import com.example.bluetooth.GlobalBluetoothStatus
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ConversationEntity
import com.example.data.local.GGDatabase
import com.example.data.local.ProfileStore
import com.example.data.model.BluetoothDevicePeer
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.GGPacket
import com.example.data.model.MessageStatus
import com.example.data.model.PacketType
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class GGViewModel(application: Application) : AndroidViewModel(application) {
    private val db = GGDatabase.getInstance(application)
    private val messageDao = db.chatMessageDao()
    private val conversationDao = db.conversationDao()
    private val profileStore = ProfileStore(application)

    val profileFlow: StateFlow<UserProfile> = profileStore.profileFlow

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeChatPeer = MutableStateFlow<BluetoothDevicePeer?>(null)
    val activeChatPeer: StateFlow<BluetoothDevicePeer?> = _activeChatPeer.asStateFlow()

    val bluetoothController: BluetoothController = BluetoothController(
        context = application,
        scope = viewModelScope,
        onMessageReceived = { msg -> handleIncomingMessage(msg) },
        onMessageDelivered = { msgId -> handleMessageAcknowledged(msgId) },
        onPeerConnected = { peer -> handlePeerConnected(peer) }
    )

    val bluetoothState: StateFlow<BluetoothUiState> = bluetoothController.uiState

    // All local conversations filtered by search query
    val conversationsFlow: StateFlow<List<Conversation>> = conversationDao.getAllConversations()
        .combine(bluetoothState) { entities, btState ->
            val connectedAddress = btState.connectedPeer?.address
            entities.map { entity ->
                entity.toModel(isOnlineNearby = entity.deviceAddress == connectedAddress)
            }
        }
        .combine(_searchQuery) { list, query ->
            if (query.isBlank()) list
            else list.filter {
                it.peerName.contains(query, ignoreCase = true) ||
                it.lastMessageText.contains(query, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Start server listening if profile already created
        val profile = profileStore.loadProfile()
        if (profile.isCreated) {
            bluetoothController.startServerListening(profile)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun saveProfile(profile: UserProfile) {
        profileStore.saveProfile(profile)
        bluetoothController.startServerListening(profile)
    }

    fun setActiveChat(peer: BluetoothDevicePeer?) {
        _activeChatPeer.value = peer
        if (peer != null) {
            viewModelScope.launch {
                conversationDao.resetUnreadCount(peer.address)
            }
        }
    }

    fun getMessagesForConversation(conversationId: String): StateFlow<List<ChatMessage>> {
        return messageDao.getMessagesForConversation(conversationId)
            .map { list -> list.map { it.toModel() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun sendMessage(conversationId: String, text: String) {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return

        val myProfile = profileFlow.value
        val messageId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val chatMessage = ChatMessage(
            messageId = messageId,
            conversationId = conversationId,
            senderName = myProfile.displayName,
            senderAddress = "ME",
            text = cleanText,
            timestamp = timestamp,
            status = MessageStatus.SENDING,
            isOutgoing = true
        )

        viewModelScope.launch {
            // 1. Save locally with SENDING status
            messageDao.insertMessage(ChatMessageEntity.fromModel(chatMessage))

            // 2. Update conversation header
            val peer = _activeChatPeer.value
            val peerName = peer?.name ?: "Nearby Contact"
            val peerMaskedPhone = peer?.maskedPhone ?: ""
            val peerAvatar = peer?.avatarId ?: 0

            val conversation = Conversation(
                conversationId = conversationId,
                peerName = peerName,
                peerMaskedPhone = peerMaskedPhone,
                peerAvatarId = peerAvatar,
                deviceAddress = conversationId,
                lastMessageText = cleanText,
                lastMessageTimestamp = timestamp,
                unreadCount = 0
            )
            conversationDao.insertOrUpdate(ConversationEntity.fromModel(conversation))

            // 3. Dispatch packet over Bluetooth transport
            val packet = GGPacket(
                type = PacketType.CHAT_MSG,
                packetId = messageId,
                senderName = myProfile.displayName,
                senderMaskedPhone = myProfile.maskedPhoneNumber,
                senderAvatar = myProfile.avatarId,
                payload = cleanText,
                timestamp = timestamp
            )

            val sentSuccessfully = bluetoothController.sendPacket(packet)
            if (sentSuccessfully) {
                // Sent to socket, awaiting delivery ACK
                messageDao.updateMessageStatus(messageId, MessageStatus.SENT.name)
            } else {
                // Socket error or peer disconnected
                messageDao.updateMessageStatus(messageId, MessageStatus.FAILED.name)
            }
        }
    }

    fun retryMessage(message: ChatMessage) {
        viewModelScope.launch {
            messageDao.updateMessageStatus(message.messageId, MessageStatus.SENDING.name)

            val myProfile = profileFlow.value
            val packet = GGPacket(
                type = PacketType.CHAT_MSG,
                packetId = message.messageId,
                senderName = myProfile.displayName,
                senderMaskedPhone = myProfile.maskedPhoneNumber,
                senderAvatar = myProfile.avatarId,
                payload = message.text,
                timestamp = message.timestamp
            )

            val sentSuccessfully = bluetoothController.sendPacket(packet)
            if (sentSuccessfully) {
                messageDao.updateMessageStatus(message.messageId, MessageStatus.SENT.name)
            } else {
                messageDao.updateMessageStatus(message.messageId, MessageStatus.FAILED.name)
            }
        }
    }

    private fun handleIncomingMessage(msg: ChatMessage) {
        viewModelScope.launch {
            // Prevent duplicate message inserts
            val alreadyExists = messageDao.hasMessage(msg.messageId)
            if (alreadyExists) return@launch

            messageDao.insertMessage(ChatMessageEntity.fromModel(msg))

            // Update conversation
            val existing = conversationDao.getConversation(msg.conversationId)
            val unread = if (_activeChatPeer.value?.address == msg.conversationId) 0 else (existing?.unreadCount ?: 0) + 1

            val conv = Conversation(
                conversationId = msg.conversationId,
                peerName = msg.senderName,
                peerMaskedPhone = existing?.peerMaskedPhone ?: "",
                peerAvatarId = existing?.peerAvatarId ?: 0,
                deviceAddress = msg.conversationId,
                lastMessageText = msg.text,
                lastMessageTimestamp = msg.timestamp,
                unreadCount = unread
            )
            conversationDao.insertOrUpdate(ConversationEntity.fromModel(conv))
        }
    }

    private fun handleMessageAcknowledged(messageId: String) {
        viewModelScope.launch {
            // Mark as DELIVERED after receiving remote ACK!
            messageDao.updateMessageStatus(messageId, MessageStatus.DELIVERED.name)
        }
    }

    private fun handlePeerConnected(peer: BluetoothDevicePeer) {
        viewModelScope.launch {
            val existing = conversationDao.getConversation(peer.address)
            if (existing == null) {
                val newConv = Conversation(
                    conversationId = peer.address,
                    peerName = peer.name,
                    peerMaskedPhone = peer.maskedPhone ?: "",
                    peerAvatarId = peer.avatarId,
                    deviceAddress = peer.address,
                    lastMessageText = "Connected offline via Bluetooth",
                    lastMessageTimestamp = System.currentTimeMillis(),
                    unreadCount = 0
                )
                conversationDao.insertOrUpdate(ConversationEntity.fromModel(newConv))
            }
        }
    }

    fun clearConversation(conversationId: String) {
        viewModelScope.launch {
            messageDao.deleteMessagesForConversation(conversationId)
            conversationDao.deleteConversation(conversationId)
            if (_activeChatPeer.value?.address == conversationId) {
                _activeChatPeer.value = null
            }
        }
    }

    fun deleteEntireAccountAndHistory() {
        viewModelScope.launch {
            bluetoothController.disconnect()
            messageDao.deleteAllMessages()
            conversationDao.deleteAllConversations()
            profileStore.clearProfile()
            _activeChatPeer.value = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.unregisterReceiver()
    }
}
