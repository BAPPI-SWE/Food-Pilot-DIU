

package com.diu.foodpilot.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.diu.foodpilot.user.screens.AuthScreen
import com.diu.foodpilot.user.ui.theme.FoodPilotDIUTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodPilotDIUTheme {
                // This state will determine which screen to show.
                // It checks Firebase Auth to see if a user is already logged in.
                var isLoggedIn by remember {
                    mutableStateOf(Firebase.auth.currentUser != null)
                }

                if (isLoggedIn) {
                    // If logged in, show the main app screen
                    MainScreen()
                } else {
                    // If not logged in, show the authentication screen
                    AuthScreen(
                        onLoginSuccess = {
                            // When login is successful, update the state to show the main screen
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}
