package com.nhatpham.task_tracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun TasktrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val taskTrackerColors = if (darkTheme) {
        TaskTrackerColors(
            gradientStart = DarkGradientStart,
            gradientMiddle = DarkGradientMiddle,
            gradientEnd = DarkGradientEnd,
            glassBackground = DarkGlassBackground,
            glassBorder = DarkGlassBorder,
            glassAccent = DarkGlassAccent,
            textPrimary = DarkTextPrimary,
            textSecondary = DarkTextSecondary,
            textMuted = DarkTextMuted,
            textDisabled = DarkTextDisabled,
            textCompleted = DarkTextCompleted,
            dialogBackground = DarkDialogBackground,
            dialogBorder = DarkDialogBorder,
            isDark = true
        )
    } else {
        TaskTrackerColors(
            gradientStart = LightGradientStart,
            gradientMiddle = LightGradientMiddle,
            gradientEnd = LightGradientEnd,
            glassBackground = LightGlassBackground,
            glassBorder = LightGlassBorder,
            glassAccent = LightGlassAccent,
            textPrimary = LightTextPrimary,
            textSecondary = LightTextSecondary,
            textMuted = LightTextMuted,
            textDisabled = LightTextDisabled,
            textCompleted = LightTextCompleted,
            dialogBackground = LightDialogBackground,
            dialogBorder = LightDialogBorder,
            isDark = false
        )
    }

    CompositionLocalProvider(LocalTaskTrackerColors provides taskTrackerColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

val LocalTaskTrackerColors = compositionLocalOf<TaskTrackerColors> {
    error("No TaskTrackerColors provided")
}
val MaterialTheme.taskTrackerColors: TaskTrackerColors
    @Composable get() = LocalTaskTrackerColors.current