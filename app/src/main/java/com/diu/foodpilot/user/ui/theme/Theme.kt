package com.diu.foodpilot.user.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// We are defining a light color scheme using our custom colors.
// We are not defining a darkColorScheme because you specified a Light Mode app.
private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    secondary = LightPink,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText,
)

@Composable
fun FoodPilotDIUTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // We'll ignore this for now
    content: @Composable () -> Unit
) {
    // We force the LightColorScheme regardless of the system theme.
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)

            // Set the status bar color using WindowInsetsControllerCompat
            // This is the preferred method.
            // Note: There isn't a direct equivalent for setting the *color* with
            // WindowInsetsControllerCompat. Instead, you influence the system UI
            // appearance. For transparent status bars with drawn-behind content,
            // you'd typically make the status bar transparent and then draw your
            // app's content behind it.
            // If you want a solid color status bar, you usually achieve this by
            // NOT making it transparent and letting the window background or a view
            // show through, or by drawing a view in that area.

            // For this specific case, if you want to tint the status bar,
            // and your app is not drawing behind the status bar (edge-to-edge is not enabled
            // for the status bar), the system will use the color specified in your theme
            // (e.g., colorPrimaryDark or android:statusBarColor in styles.xml for View-based themes,
            // or indirectly via Material 3 theming).

            // If you *are* drawing edge-to-edge, then you'd typically make the status bar
            // transparent and draw your own background behind it.

            // The line you had:
            // window.statusBarColor = colorScheme.primary.toArgb()
            // This directly sets the color. If you still need this for older APIs,
            // you can conditionally add it, but prefer using themes and WindowInsetsControllerCompat.

            // Example for older APIs (if absolutely needed):
            // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // Or another appropriate API level
            //    window.statusBarColor = colorScheme.primary.toArgb()
            // }


            // Set the status bar icons to be light or dark based on the background color
            // If your primary color is dark, you want light status bar icons (isAppearanceLightStatusBars = false).
            // If your primary color is light, you want dark status bar icons (isAppearanceLightStatusBars = true).
            insetsController.isAppearanceLightStatusBars = false // Set to true if your primary color is light
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // This 'Typography' comes from the Typography.kt file
        content = content
    )
}