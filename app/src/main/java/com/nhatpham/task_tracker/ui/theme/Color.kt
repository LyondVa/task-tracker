package com.nhatpham.task_tracker.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val LightGradientStart = Color(0xFFE8F4FD)
val LightGradientMiddle = Color(0xFFF0E6FF)
val LightGradientEnd = Color(0xFFFFF2F8)

val DarkGradientStart = Color(0xFF1E1B2E)
val DarkGradientMiddle = Color(0xFF2A2438)
val DarkGradientEnd = Color(0xFF1F1D2B)

val LightGlassBackground = Color.White.copy(alpha = 0.4f)
val LightGlassBorder = Color(0xFFE1E8ED).copy(alpha = 0.6f)
val LightGlassAccent = Color(0xFFF8FAFC).copy(alpha = 0.8f)

val DarkGlassBackground = Color(0xFF2D2A3D).copy(alpha = 0.7f)
val DarkGlassBorder = Color(0xFF4A4458).copy(alpha = 0.5f)
val DarkGlassAccent = Color(0xFF363248).copy(alpha = 0.6f)

val LightDialogBackground = Color(0xFFFDFDFE)
val DarkDialogBackground = Color(0xFF2C2937)
val LightDialogBorder = Color(0xFFE5E7EB)
val DarkDialogBorder = Color(0xFF4C4A57)

val LightTextPrimary = Color(0xFF2D3748)
val LightTextSecondary = Color(0xFF4A5568)
val LightTextMuted = Color(0xFF718096)
val LightTextDisabled = Color(0xFFA0AEC0)
val LightTextCompleted = Color(0xFFBBD0E8)

val DarkTextPrimary = Color(0xFFF7FAFC)
val DarkTextSecondary = Color(0xFFE2E8F0)
val DarkTextMuted = Color(0xFFB8C5D1)
val DarkTextDisabled = Color(0xFF8B94A3)
val DarkTextCompleted = Color(0xFF6B7C93)

data class TaskTrackerColors(
    val gradientStart: Color,
    val gradientMiddle: Color,
    val gradientEnd: Color,
    val glassBackground: Color,
    val glassBorder: Color,
    val glassAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textDisabled: Color,
    val textCompleted: Color,
    val dialogBackground: Color,
    val dialogBorder: Color,
    val isDark: Boolean
)