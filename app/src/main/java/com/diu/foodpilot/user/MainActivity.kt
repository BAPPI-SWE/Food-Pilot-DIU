

package com.diu.foodpilot.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.diu.foodpilot.user.ui.theme.FoodPilotDIUTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodPilotDIUTheme {
                MainScreen()
            }
        }
    }
}
