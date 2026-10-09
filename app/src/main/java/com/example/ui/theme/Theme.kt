package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GGDarkColorScheme = darkColorScheme(
    primary = GGElectricGreen,
    onPrimary = GGOnGreen,
    primaryContainer = GGElectricGreenContainer,
    onPrimaryContainer = GGElectricGreenNeon,
    secondary = GGElectricGreenDim,
    onSecondary = GGBlack,
    secondaryContainer = GGDarkSurfaceHighlight,
    onSecondaryContainer = GGTextPrimary,
    background = GGDarkBackground,
    onBackground = GGTextPrimary,
    surface = GGDarkSurface,
    onSurface = GGTextPrimary,
    surfaceVariant = GGDarkSurfaceVariant,
    onSurfaceVariant = GGTextSecondary,
    outline = GGBorder,
    outlineVariant = GGBorderLight,
    error = GGStatusError,
    onError = GGTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // GG is designed around a premium dark interface
    dynamicColor: Boolean = false, // Keep branded electric green accents
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GGDarkColorScheme,
        typography = Typography,
        content = content
    )
}
