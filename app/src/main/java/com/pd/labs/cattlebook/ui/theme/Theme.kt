package com.pd.labs.cattlebook.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Green700,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Green700,
    secondary = Amber700,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Amber100,
    onSecondaryContainer = Amber700,
    error = Red600,
    background = Surface,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
)

@Composable
fun CattleBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
