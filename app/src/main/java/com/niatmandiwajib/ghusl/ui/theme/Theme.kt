package com.niatmandiwajib.ghusl.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GhuslTypography,
        content = content
    )
}
