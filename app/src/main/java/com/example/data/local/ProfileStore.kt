package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("gg_profile_store", Context.MODE_PRIVATE)

    private val _profileFlow = MutableStateFlow(loadProfile())
    val profileFlow: StateFlow<UserProfile> = _profileFlow.asStateFlow()

    fun loadProfile(): UserProfile {
        val isCreated = prefs.getBoolean(KEY_IS_CREATED, false)
        if (!isCreated) {
            return UserProfile(isCreated = false)
        }
        return UserProfile(
            displayName = prefs.getString(KEY_DISPLAY_NAME, "") ?: "",
            mobileNumber = prefs.getString(KEY_MOBILE_NUMBER, "") ?: "",
            countryCode = prefs.getString(KEY_COUNTRY_CODE, "+91") ?: "+91",
            avatarId = prefs.getInt(KEY_AVATAR_ID, 0),
            isCreated = true,
            createdAt = prefs.getLong(KEY_CREATED_AT, System.currentTimeMillis())
        )
    }

    fun saveProfile(profile: UserProfile) {
        prefs.edit()
            .putString(KEY_DISPLAY_NAME, profile.displayName.trim())
            .putString(KEY_MOBILE_NUMBER, profile.mobileNumber.trim())
            .putString(KEY_COUNTRY_CODE, profile.countryCode)
            .putInt(KEY_AVATAR_ID, profile.avatarId)
            .putBoolean(KEY_IS_CREATED, true)
            .putLong(KEY_CREATED_AT, profile.createdAt)
            .apply()

        _profileFlow.value = profile.copy(isCreated = true)
    }

    fun clearProfile() {
        prefs.edit().clear().apply()
        _profileFlow.value = UserProfile(isCreated = false)
    }

    companion object {
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_MOBILE_NUMBER = "mobile_number"
        private const val KEY_COUNTRY_CODE = "country_code"
        private const val KEY_AVATAR_ID = "avatar_id"
        private const val KEY_IS_CREATED = "is_created"
        private const val KEY_CREATED_AT = "created_at"
    }
}
