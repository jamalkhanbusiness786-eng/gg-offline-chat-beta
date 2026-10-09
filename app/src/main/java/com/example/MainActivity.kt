package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BluetoothDevicePeer
import com.example.ui.components.ConnectionRequestDialog
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.GGDarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GGViewModel

enum class AppScreen {
    WELCOME,
    PROFILE_SETUP,
    HOME,
    EDIT_PROFILE,
    CHAT_DETAIL
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = GGDarkBackground
                ) {
                    GGAppRoot()
                }
            }
        }
    }
}

@Composable
fun GGAppRoot(viewModel: GGViewModel = viewModel()) {
    val profile by viewModel.profileFlow.collectAsState()
    val bluetoothState by viewModel.bluetoothState.collectAsState()

    var currentScreen by remember(profile.isCreated) {
        mutableStateOf(if (profile.isCreated) AppScreen.HOME else AppScreen.WELCOME)
    }

    var selectedChatPeer by remember { mutableStateOf<BluetoothDevicePeer?>(null) }

    // Intercept hardware Back Button
    BackHandler(enabled = currentScreen != AppScreen.HOME && currentScreen != AppScreen.WELCOME) {
        when (currentScreen) {
            AppScreen.PROFILE_SETUP -> currentScreen = AppScreen.WELCOME
            AppScreen.EDIT_PROFILE -> currentScreen = AppScreen.HOME
            AppScreen.CHAT_DETAIL -> {
                selectedChatPeer = null
                viewModel.setActiveChat(null)
                currentScreen = AppScreen.HOME
            }
            else -> {}
        }
    }

    Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
        when (screen) {
            AppScreen.WELCOME -> {
                WelcomeScreen(
                    onCreateProfileClick = { currentScreen = AppScreen.PROFILE_SETUP }
                )
            }
            AppScreen.PROFILE_SETUP -> {
                ProfileSetupScreen(
                    initialProfile = null,
                    isEditing = false,
                    onBack = { currentScreen = AppScreen.WELCOME },
                    onSaveProfile = { newProfile ->
                        viewModel.saveProfile(newProfile)
                        currentScreen = AppScreen.HOME
                    }
                )
            }
            AppScreen.HOME -> {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenChat = { peer ->
                        selectedChatPeer = peer
                        viewModel.setActiveChat(peer)
                        currentScreen = AppScreen.CHAT_DETAIL
                    },
                    onEditProfile = { currentScreen = AppScreen.EDIT_PROFILE }
                )
            }
            AppScreen.EDIT_PROFILE -> {
                ProfileSetupScreen(
                    initialProfile = profile,
                    isEditing = true,
                    onBack = { currentScreen = AppScreen.HOME },
                    onSaveProfile = { updated ->
                        viewModel.saveProfile(updated)
                        currentScreen = AppScreen.HOME
                    }
                )
            }
            AppScreen.CHAT_DETAIL -> {
                selectedChatPeer?.let { peer ->
                    ChatDetailScreen(
                        peer = peer,
                        viewModel = viewModel,
                        onBack = {
                            selectedChatPeer = null
                            viewModel.setActiveChat(null)
                            currentScreen = AppScreen.HOME
                        }
                    )
                } ?: run {
                    currentScreen = AppScreen.HOME
                }
            }
        }
    }

    // Global Incoming Connection Request Dialog
    bluetoothState.incomingConnectionRequest?.let { request ->
        ConnectionRequestDialog(
            request = request,
            onAccept = {
                viewModel.bluetoothController.acceptIncomingConnection(profile)
                // Automatically open chat with accepted peer
                val peer = BluetoothDevicePeer(
                    address = request.peerAddress,
                    name = request.peerName,
                    maskedPhone = request.peerMaskedPhone,
                    avatarId = request.peerAvatarId
                )
                selectedChatPeer = peer
                viewModel.setActiveChat(peer)
                currentScreen = AppScreen.CHAT_DETAIL
            },
            onDecline = {
                viewModel.bluetoothController.declineIncomingConnection()
            }
        )
    }
}
