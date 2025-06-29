// Open this file: app/src/main/java/com/diu/foodpilot/user/ui/theme/Theme.kt
// Replace its contents with this updated version.

package com.diu.foodpilot.user.ui.theme

import android.app.Activity
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    background = BackgroundWhite,
    surface = SurfaceGrey,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun FoodPilotDIUTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // THE FIX: We make the status bar transparent
            window.statusBarColor = AndroidColor.TRANSPARENT
            // This tells the system that our content will handle drawing behind the status bar
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // This ensures the status bar icons (time, battery) are visible against our red header
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
