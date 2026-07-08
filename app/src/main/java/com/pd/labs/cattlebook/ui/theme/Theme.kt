package com.pd.labs.cattlebook.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary              = Green700,
    onPrimary            = Color.White,
    primaryContainer     = Green100,
    onPrimaryContainer   = Green900,
    secondary            = Amber700,
    onSecondary          = Color.White,
    secondaryContainer   = Amber100,
    onSecondaryContainer = Amber800,
    tertiary             = Teal700,
    onTertiary           = Color.White,
    tertiaryContainer    = Teal100,
    onTertiaryContainer  = Teal700,
    error                = Red600,
    onError              = Color.White,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Red600,
    background           = Surface,
    onBackground         = OnSurface,
    surface              = Surface,
    onSurface            = OnSurface,
    surfaceVariant       = SurfaceVar,
    onSurfaceVariant     = Color(0xFF44403C),
)

@Composable
fun CattleBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
