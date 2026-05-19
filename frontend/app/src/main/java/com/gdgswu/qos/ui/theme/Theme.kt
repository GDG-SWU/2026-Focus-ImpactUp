package com.gdgswu.qos.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val QOSColorScheme = lightColorScheme(
    primary = QOSRed,
    onPrimary = White,
    secondary = QOSCyan,
    onSecondary = White,
    tertiary = QOSNavy,
    background = BackgroundGray,
    surface = White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun QOSTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }
    MaterialTheme(
        colorScheme = QOSColorScheme,
        typography = Typography,
        content = content
    )
}
