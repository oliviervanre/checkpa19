package com.example.pa19checklist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = CockpitBlue,
    secondary = SignalAmber,
    background = PaperWhite,
    surface = PanelWhite,
    surfaceVariant = MutedGray,
    onPrimary = PanelWhite,
    onSecondary = CockpitBlue,
    onBackground = CockpitBlue,
    onSurface = CockpitBlue,
    onSurfaceVariant = TextGray
)

@Composable
fun PA19ChecklistTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
