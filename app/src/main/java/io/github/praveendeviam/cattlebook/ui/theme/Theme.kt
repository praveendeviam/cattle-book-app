package io.github.praveendeviam.cattlebook.ui.theme

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
    errorContainer       = Color(0xFFFEE2E2),
    onErrorContainer     = Red600,
    background           = Surface,
    onBackground         = OnSurface,
    surface              = Color.White,   // cards float above cream
    onSurface            = OnSurface,
    surfaceVariant       = SurfaceVar,
    onSurfaceVariant     = Color(0xFF57534E),
    outline              = Color(0xFFD6D3D1),
)

@Composable
fun CattleBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
