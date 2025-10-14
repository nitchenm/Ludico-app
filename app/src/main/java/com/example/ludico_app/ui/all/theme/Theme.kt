package com.example.ludico_app.ui.all.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LudicoGreen,                 // El color de acento se mantiene
    onPrimary = Black,
    secondary = LudicoLightGray,           // El secundario ahora es claro
    onSecondary = Black,
    background = LudicoDark,               // El fondo general es oscuro
    onBackground = White,                  // El texto general es blanco
    surface = Color(0xFF2C2C2C),           // Las tarjetas son un poco más claras que el fondo
    onSurface = White,
    surfaceVariant = Color(0xFF3E3E3E),   // Los campos de texto son un poco más claros
    onSurfaceVariant = Color(0xFFB0B0B0)   // El texto placeholder es gris claro
)


private val LightColorScheme = lightColorScheme(
    primary = LudicoGreen,                 // Botones principales, FABs
    onPrimary = Black,                     // Texto sobre botones verdes (Negro es más legible)
    secondary = LudicoDark,                // Botones secundarios, texto importante
    onSecondary = White,                   // Texto sobre elementos oscuros
    background = Color(0xFFFAFAFA),        // Fondo general ligeramente grisáceo
    onBackground = LudicoDark,             // Texto sobre el fondo general
    surface = White,                       // Color para las tarjetas principales
    onSurface = LudicoDark,                // Texto sobre las tarjetas
    surfaceVariant = LudicoLightGray,      // Fondo de campos de texto, filtros
    onSurfaceVariant = Gray                // Texto placeholder sobre los campos de texto
)

@Composable
fun LudicoappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.secondary.toArgb() // Barra de estado oscura
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Texto de la barra blanco
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}