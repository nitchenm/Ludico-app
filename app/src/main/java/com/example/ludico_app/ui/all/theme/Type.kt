package com.example.ludico_app.ui.all.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Defines the typography for the app, now using a classic Serif font for a fantasy feel.
val Typography = Typography(
    // Default body text, like descriptions.
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Serif, // Switched to a classic, book-like font.
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Titles for event cards and screens.
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif, // Serif for titles.
        fontWeight = FontWeight.Bold, // Bold for importance.
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Smaller text for labels or secondary info.
    labelSmall = TextStyle(
        fontFamily = FontFamily.Serif, // Serif for consistency.
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Headline for major screen titles.
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )
)
