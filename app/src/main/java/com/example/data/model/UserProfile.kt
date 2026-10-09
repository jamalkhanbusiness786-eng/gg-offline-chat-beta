package com.example.data.model

data class UserProfile(
    val displayName: String = "",
    val mobileNumber: String = "",
    val countryCode: String = "+91",
    val avatarId: Int = 0,
    val isCreated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Masked phone number to never reveal full private number to nearby users.
     * E.g. "+91 ••••• ••421" or "+1 ••••• ••89"
     */
    val maskedPhoneNumber: String
        get() {
            val digits = mobileNumber.filter { it.isDigit() }
            return if (digits.length >= 4) {
                val last3 = digits.takeLast(3)
                "$countryCode ••••• ••$last3"
            } else if (digits.isNotEmpty()) {
                "$countryCode •••••"
            } else {
                "$countryCode (Unverified)"
            }
        }
}

val PRESET_AVATARS = listOf(
    "⚡", // Electric Beacon
    "🟢", // Lime Core
    "📡", // Mesh Satellite
    "🐺", // Cyber Wolf
    "🛸", // P2P Saucer
    "🛡️", // Shield Guard
    "🦊", // Neon Fox
    "🔥"  // Ember Spark
)
