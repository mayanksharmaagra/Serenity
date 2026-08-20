package com.jrprofessor.serenity.ui.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SerenityColorScheme = darkColorScheme(
    primary = AccentLavender,
    onPrimary = TextOnAccent,
    primaryContainer = AccentLavenderDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentCoral,
    onSecondary = TextOnAccent,
    tertiary = AccentMint,
    onTertiary = TextOnAccent,
    background = BgGradientStart,
    onBackground = TextPrimary,
    surface = BgGradientMid,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurface,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder
)

@Composable
fun SerenityTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = SerenityColorScheme,
        typography = Typography,
        content = content
    )
}

fun Modifier.serenityBackground(): Modifier = this
    .fillMaxSize()
    .background(BackgroundBrush)