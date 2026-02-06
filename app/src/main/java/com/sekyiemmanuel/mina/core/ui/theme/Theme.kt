package com.sekyiemmanuel.mina.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MinaLightColors = lightColorScheme(
    primary = NeutralText,
    onPrimary = SurfacePill,
    secondary = AccentFlame,
    background = CanvasBackground,
    surface = SurfacePill,
    onBackground = NeutralText,
    onSurface = NeutralText,
)

@Composable
fun MinaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MinaLightColors,
        typography = AppTypography,
        content = content,
    )
}

