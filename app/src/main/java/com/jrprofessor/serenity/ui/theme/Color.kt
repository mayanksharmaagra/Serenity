package com.jrprofessor.serenity.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Background Gradients
val BgGradientStart = Color(0xFF100D1E)
val BgGradientMid = Color(0xFF16122C)
val BgGradientEnd = Color(0xFF1E1838)

// Glass Surfaces & Borders
val GlassSurface = Color(0x14FFFFFF) // 8% white
val GlassSurfaceLight = Color(0x1FFFFFFF) // 12% white
val GlassSurfaceDark = Color(0x28000000) // 16% black
val GlassBorder = Color(0x20FFFFFF) // 12.5% white
val GlassBorderLight = Color(0x38FFFFFF) // 22% white
val GlassBorderGlow = Color(0x60C9A9E9)

// Accent Colors
val AccentCoral = Color(0xFFFF6F61)
val AccentCoralLight = Color(0xFFFF8A80)
val AccentLavender = Color(0xFFC9A9E9)
val AccentLavenderLight = Color(0xFFE1D0FA)
val AccentLavenderDark = Color(0xFF8C6CB8)
val AccentMint = Color(0xFF7FE0B4)
val AccentMintLight = Color(0xFFA5F3CE)
val AccentYellow = Color(0xFFFFC94A) // Glowing selected mood ring

// Stress / Calm Indicators
val StressPillBg = Color(0x33FF6F61)
val StressPillText = Color(0xFFFF8A80)
val CalmPillBg = Color(0x337FE0B4)
val CalmPillText = Color(0xFF7FE0B4)

// Text Colors
val TextPrimary = Color(0xFFF5F3FA)
val TextSecondary = Color(0xFFA9A4C0)
val TextMuted = Color(0xFF7A7596)
val TextOnAccent = Color(0xFF100D1E)

// Button & Pill Gradients
val PrimaryButtonBrush = Brush.horizontalGradient(
    colors = listOf(AccentCoral, AccentLavender)
)

val PrimaryButtonDisabledBrush = Brush.horizontalGradient(
    colors = listOf(Color(0x24FF6F61), Color(0x24C9A9E9))
)

val StressBarBrush = Brush.horizontalGradient(
    colors = listOf(AccentCoralLight, AccentCoral, Color(0xFFFF9E80))
)

val CalmBarBrush = Brush.horizontalGradient(
    colors = listOf(AccentMintLight, AccentMint, Color(0xFF64FFDA))
)

val BackgroundBrush = Brush.verticalGradient(
    colors = listOf(BgGradientStart, BgGradientMid, BgGradientEnd)
)

val GlassNavBrush = Brush.verticalGradient(
    colors = listOf(Color(0xCC18142E), Color(0xF0120F24))
)

val MoodGlowColor = AccentYellow.copy(alpha = 0.5f)