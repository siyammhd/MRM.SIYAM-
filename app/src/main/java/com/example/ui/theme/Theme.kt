package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SleekDarkPrimary,
    secondary = SleekDarkSecondary,
    tertiary = SleekDarkTertiary,
    background = SleekDarkBg,
    surface = SleekDarkCard,
    surfaceVariant = SleekDarkSurface,
    onPrimary = SleekDarkOnPrimary,
    onSecondary = Color(0xFF130E20),
    onTertiary = Color.White,
    onBackground = SleekDarkText,
    onSurface = SleekDarkText,
    onSurfaceVariant = SleekDarkText
)

private val LightColorScheme = lightColorScheme(
    primary = SleekPurplePrimary,
    secondary = SleekPurpleSecondary,
    tertiary = SleekPurpleTertiary,
    background = SleekLightBg,
    surface = SleekLightCard,
    surfaceVariant = SleekLightSurface,
    onPrimary = Color.White,
    onSecondary = Color(0xFF21005D),
    onTertiary = Color(0xFF21005D),
    onBackground = SleekLightText,
    onSurface = SleekLightText,
    onSurfaceVariant = Color(0xFF49454F)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
