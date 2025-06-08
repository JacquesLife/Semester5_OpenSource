package com.example.budgettrackerapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = Blue40,
    secondary = BlueGrey40,
    tertiary = LightBlue40,
    background = Color(0xFF0D1117), // Darker, more neutral background
    surface = Color(0xFF161B22),    // Slightly lighter surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE6EDF3), // Better contrast
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFFE6EDF3),
    outline = Color(0xFF30363D),
    surfaceContainer = Color(0xFF161B22),
    surfaceContainerHigh = Color(0xFF21262D),
    surfaceContainerHighest = Color(0xFF30363D)
)

val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = BlueGrey40,
    tertiary = LightBlue40,
    background = Color(0xFFFDFDFD),  // Pure white background
    surface = Color(0xFFFFFFFF),     // Pure white surface
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF0D1117), // Dark text on light background
    onSurface = Color(0xFF0D1117),
    surfaceVariant = Color(0xFFF6F8FA),
    onSurfaceVariant = Color(0xFF0D1117),
    outline = Color(0xFFD1D9E0),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFF6F8FA),
    surfaceContainerHighest = Color(0xFFEAEEF2)
)

@Composable
fun BudgetTrackerAppTheme(
    darkTheme: Boolean = ThemeManager.isDarkModeEnabled(),
    // Always set to false to maintain our custom blue theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Configure the color scheme based on darkTheme
    val colorScheme = ThemeManager.getCurrentTheme()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}