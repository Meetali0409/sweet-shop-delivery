package com.sweetshop.admin.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AdminColorScheme = lightColorScheme(
    primary = AdminPrimary,
    onPrimary = AdminOnPrimary,
    primaryContainer = AdminPrimaryLight,
    onPrimaryContainer = AdminPrimaryDark,
    secondary = AdminSecondary,
    onSecondary = AdminOnSecondary,
    background = AdminBackground,
    onBackground = AdminOnBackground,
    surface = AdminSurface,
    onSurface = AdminOnSurface,
    error = AdminError,
    onError = AdminOnPrimary,
    surfaceVariant = AdminSurface,
    outline = AdminPrimaryLight
)

@Composable
fun SweetShopAdminTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AdminPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = AdminColorScheme,
        typography = AdminTypography,
        content = content
    )
}
