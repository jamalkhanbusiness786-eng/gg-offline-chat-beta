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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PRESET_AVATARS
import com.example.data.model.UserProfile
import com.example.ui.theme.GGBorder
import com.example.ui.theme.GGDarkBackground
import com.example.ui.theme.GGDarkSurface
import com.example.ui.theme.GGDarkSurfaceVariant
import com.example.ui.theme.GGElectricGreen
import com.example.ui.theme.GGElectricGreenContainer
import com.example.ui.theme.GGOnGreen
import com.example.ui.theme.GGTextMuted
import com.example.ui.theme.GGTextPrimary
import com.example.ui.theme.GGTextSecondary

private val COMMON_COUNTRY_CODES = listOf(
    "+91" to "India (+91)",
    "+1" to "US/Canada (+1)",
    "+44" to "UK (+44)",
    "+61" to "Australia (+61)",
    "+971" to "UAE (+971)",
    "+49" to "Germany (+49)",
    "+33" to "France (+33)",
    "+81" to "Japan (+81)",
    "+65" to "Singapore (+65)",
    "+55" to "Brazil (+55)"
)

@Composable
fun ProfileSetupScreen(
    initialProfile: UserProfile? = null,
    isEditing: Boolean = false,
    onBack: (() -> Unit)? = null,
    onSaveProfile: (UserProfile) -> Unit
) {
    var displayName by remember { mutableStateOf(initialProfile?.displayName ?: "") }
    var mobileNumber by remember { mutableStateOf(initialProfile?.mobileNumber ?: "") }
    var countryCode by remember { mutableStateOf(initialProfile?.countryCode ?: "+91") }
    var selectedAvatarId by remember { mutableIntStateOf(initialProfile?.avatarId ?: 0) }
    var countryMenuExpanded by remember { mutableStateOf(false) }

    var displayNameError by remember { mutableStateOf(false) }
    var mobileError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GGDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        if (isEditing && onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("profile_back_button")
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = GGTextPrimary
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = if (isEditing) "Edit Profile" else "Create Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = GGTextPrimary
        )

        Text(
            text = "Your profile is stored strictly on this device. No remote servers, no tracking.",
            fontSize = 14.sp,
            color = GGTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // Avatar Picker
        Text(
            text = "CHOOSE AVATAR (OPTIONAL)",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = GGElectricGreen,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(PRESET_AVATARS) { index, avatar ->
                val isSelected = selectedAvatarId == index
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) GGElectricGreenContainer else GGDarkSurface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) GGElectricGreen else GGBorder,
                            shape = CircleShape
                        )
                        .clickable { selectedAvatarId = index }
                        .testTag("avatar_option_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = avatar, fontSize = 24.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Display Name Field
        Text(
            text = "DISPLAY NAME *",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = GGElectricGreen,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = displayName,
            onValueChange = {
                displayName = it
                displayNameError = false
            },
            placeholder = { Text("e.g. Arjun", color = GGTextMuted) },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = GGElectricGreen)
            },
            isError = displayNameError,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("display_name_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GGDarkSurface,
                unfocusedContainerColor = GGDarkSurface,
                focusedBorderColor = GGElectricGreen,
                unfocusedBorderColor = GGBorder,
                focusedTextColor = GGTextPrimary,
                unfocusedTextColor = GGTextPrimary
            )
        )
        if (displayNameError) {
            Text(
                text = "Display name is required.",
                color = Color(0xFFFF5252),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mobile Number Field with Country Calling Code Dropdown
        Text(
            text = "MOBILE NUMBER *",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = GGElectricGreen,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Country Code Selector
            Box {
                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(GGDarkSurface)
                        .border(1.dp, GGBorder, RoundedCornerShape(14.dp))
                        .clickable { countryMenuExpanded = true }
                        .padding(horizontal = 14.dp)
                        .testTag("country_code_selector"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = countryCode,
                        color = GGTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                DropdownMenu(
                    expanded = countryMenuExpanded,
                    onDismissRequest = { countryMenuExpanded = false },
                    modifier = Modifier.background(GGDarkSurfaceVariant)
                ) {
                    COMMON_COUNTRY_CODES.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label, color = GGTextPrimary) },
                            onClick = {
                                countryCode = code
                                countryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Phone Input
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = {
                    mobileNumber = it.filter { char -> char.isDigit() || char == ' ' }
                    mobileError = false
                },
                placeholder = { Text("98765 43210", color = GGTextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = GGElectricGreen)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = mobileError,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("mobile_number_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GGDarkSurface,
                    unfocusedContainerColor = GGDarkSurface,
                    focusedBorderColor = GGElectricGreen,
                    unfocusedBorderColor = GGBorder,
                    focusedTextColor = GGTextPrimary,
                    unfocusedTextColor = GGTextPrimary
                )
            )
        }

        if (mobileError) {
            Text(
                text = "Mobile number is required.",
                color = Color(0xFFFF5252),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 6.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy & Unverified Number Disclosure Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GGDarkSurfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, GGBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = GGElectricGreen,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Zero OTP • Privacy First",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GGTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Because GG operates 100% offline, mobile numbers are NOT verified via SMS or OTP. Your full mobile number is NEVER broadcast or displayed to nearby peers. Instead, only a masked ID (e.g. $countryCode ••••• ••${mobileNumber.takeLast(3).ifEmpty { "•••" }}) is shown.",
                        fontSize = 12.sp,
                        color = GGTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Submit Button
        Button(
            onClick = {
                val nameClean = displayName.trim()
                val phoneClean = mobileNumber.trim()
                var hasErr = false
                if (nameClean.isEmpty()) {
                    displayNameError = true
                    hasErr = true
                }
                if (phoneClean.isEmpty()) {
                    mobileError = true
                    hasErr = true
                }
                if (!hasErr) {
                    val profile = UserProfile(
                        displayName = nameClean,
                        mobileNumber = phoneClean,
                        countryCode = countryCode,
                        avatarId = selectedAvatarId,
                        isCreated = true,
                        createdAt = initialProfile?.createdAt ?: System.currentTimeMillis()
                    )
                    onSaveProfile(profile)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("start_chatting_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GGElectricGreen,
                contentColor = GGOnGreen
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = if (isEditing) "SAVE CHANGES" else "START CHATTING",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
