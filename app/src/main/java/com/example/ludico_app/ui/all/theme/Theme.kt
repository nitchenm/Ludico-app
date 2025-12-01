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
    primary = SealingWaxRed,
    onPrimary = Parchment,
    secondary = Parchment,
    onSecondary = Ink,
    background = DarkLeather,
    onBackground = Parchment,
    surface = DarkLeather.copy(alpha = 0.8f),
    onSurface = Parchment,
    surfaceVariant = Ink,
    onSurfaceVariant = Gray,
    error = ErrorRedDark,
    onError = Parchment
)

// "Adventurer's Journal" - Light, clean, and classic theme
private val LightColorScheme = lightColorScheme(
    primary = SealingWaxRed,
    onPrimary = White,
    secondary = Ink,
    onSecondary = White,
    background = Parchment,
    onBackground = Ink,
    surface = White.copy(alpha = 0.9f),
    onSurface = Ink,
    surfaceVariant = LightGray,
    onSurfaceVariant = Gray,
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
