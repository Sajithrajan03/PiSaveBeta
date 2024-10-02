package com.sajithrajan.pisave.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LightColorPalette = lightColorScheme(
    primary = LightBlue,
    secondary = BrightGreen,
    background = LightBlue,
    surface = DarkBlue,
    surfaceTint = LightGray,
    onPrimary = White,
    onSecondary = LightGray,
    onBackground = White,
    onSurface = LightGray,
)

val DarkColorPalette = darkColorScheme(
    primary = LightBlue,
    secondary = NavyBlue,
    background = LightBlue,
    surface = DarkBlue,
    surfaceTint = LightGray,
    onPrimary = White,
    onSecondary = LightGray,
    onBackground = White,
    onSurface = LightGray,

)


@Composable
fun PiSaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}