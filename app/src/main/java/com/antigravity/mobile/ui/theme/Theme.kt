package com.antigravity.mobile.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Google Gemini Official Palette
val GeminiBg = Color(0xFF131314)
val GeminiSurface = Color(0xFF1E1F20)
val GeminiSurfaceLight = Color(0xFF282A2C)
val GeminiUserBubble = Color(0xFF2B2D30)
val GeminiText = Color(0xFFE3E3E3)
val GeminiTextSecondary = Color(0xFF9E9E9E)

// Gemini Signature Gradient Colors
val GeminiBlue = Color(0xFF4285F4)
val GeminiPurple = Color(0xFF9B72CB)
val GeminiCoral = Color(0xFFD96570)
val GeminiCyan = Color(0xFF00E5FF)

val GeminiSparkleGradient = Brush.linearGradient(
    colors = listOf(GeminiBlue, GeminiPurple, GeminiCoral)
)

val DarkColorScheme = darkColorScheme(
    primary = GeminiBlue,
    secondary = GeminiPurple,
    tertiary = GeminiCyan,
    background = GeminiBg,
    surface = GeminiSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = GeminiText,
    onSurface = GeminiText
)

@Composable
fun AntigravityTheme(
    darkTheme: Boolean = true, // Default to Gemini Dark
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
