package com.example.airdry.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2F5233),
    onPrimary = Color.White,
    secondary = Color(0xFF8B5E3C),
    onSecondary = Color.White,
    tertiary = Color(0xFFD4A373),
    onTertiary = Color.Black,
    background = Color(0xFFF9F9F9),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    error = Color(0xFFB3261E),
    onError = Color.White
)

// --- Dark Color Scheme ---
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA1C398),
    onPrimary = Color(0xFF1A1A1A),
    secondary = Color(0xFFC69C72),
    onSecondary = Color(0xFF1A1A1A),
    tertiary = Color(0xFFE2C29A),
    onTertiary = Color(0xFF1A1A1A),
    background = Color(0xFF121212),
    onBackground = Color(0xFFEAEAEA),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFEAEAEA),
    error = Color(0xFFF28B82),
    onError = Color(0xFF1A1A1A)
)

@Composable
fun AirDryTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}