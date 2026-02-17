package com.example.storageapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    primaryContainer = PrimaryOrangeDark,
    onPrimaryContainer = Color.White,
    
    background = BackgroundLight,
    onBackground = TextPrimary,
    
    surface = SurfaceLight,
    onSurface = TextPrimary,
    
    error = ErrorRed,
    onError = Color.White,
    
    outline = BorderColor
)

@Composable
fun StorageAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColors // 强制使用浅色主题

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
