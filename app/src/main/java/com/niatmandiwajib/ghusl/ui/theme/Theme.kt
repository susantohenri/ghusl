package com.niatmandiwajib.ghusl.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GhuslGreenPrimary,
    onPrimary = GhuslOnPrimary,
    primaryContainer = GhuslGreenContainer,
    secondary = GhuslBlueSecondary,
    onSecondary = GhuslOnSecondary,
    secondaryContainer = GhuslBlueContainer,
    surface = GhuslSurface,
    onSurface = GhuslOnSurface,
    background = GhuslBackground,
    onBackground = GhuslOnBackground,
    error = GhuslError,
    onError = GhuslOnError,
    outline = GhuslOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = GhuslGreenPrimaryDark,
    onPrimary = GhuslOnPrimaryDark,
    primaryContainer = GhuslGreenContainerDark,
    secondary = GhuslBlueSecondaryDark,
    onSecondary = GhuslOnSecondaryDark,
    secondaryContainer = GhuslBlueContainerDark,
    surface = GhuslSurfaceDark,
    onSurface = GhuslOnSurfaceDark,
    background = GhuslBackgroundDark,
    onBackground = GhuslOnBackgroundDark,
    error = GhuslErrorDark,
    onError = GhuslOnErrorDark,
    outline = GhuslOutlineDark
)

@Composable
fun GhuslTheme(
    themeMode: String = "system",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GhuslTypography,
        content = content
    )
}
