package com.example.ludico_app.ui.all.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// "Tavern Night" - Dark, cozy, and rustic theme
private val DarkColorScheme = darkColorScheme(
    primary = SealingWaxRed,              // The main accent color, like a wax seal
    onPrimary = Parchment,                  // Text on the main accent color
    secondary = Parchment,                  // Secondary actions, like icons or highlights
    onSecondary = Ink,                      // Text on parchment-colored elements
    background = DarkLeather,               // The main app background, like a tavern table
    onBackground = Parchment,               // Main text color, perfectly readable
    surface = DarkLeather.copy(alpha = 0.8f),// Slightly lighter leather for cards
    onSurface = Parchment,                  // Text on cards
    surfaceVariant = Ink,                   // Darker ink color for dividers or text fields
    onSurfaceVariant = Gray,                // Lighter text on ink-colored fields
    error = ErrorRedDark,
    onError = Parchment
)

// "Adventurer's Journal" - Light, clean, and classic theme
private val LightColorScheme = lightColorScheme(
    primary = SealingWaxRed,              // Main accent color
    onPrimary = White,                      // Text on red buttons
    secondary = Ink,                        // Secondary color for text and icons
    onSecondary = White,                    // Text on dark ink elements
    background = Parchment,                 // The feeling of an old journal page
    onBackground = Ink,                     // Main text color
    surface = White.copy(alpha = 0.9f),   // Cards are slightly off-white
    onSurface = Ink,                        // Text on cards
    surfaceVariant = LightGray,             // Subtle backgrounds for text fields
    onSurfaceVariant = Gray,                // Placeholder text
    error = ErrorRedLight,
    onError = White
)

@Composable
fun LudicoappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
