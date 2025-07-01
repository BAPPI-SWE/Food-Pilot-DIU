// Open this file: app/src/main/java/com/diu/foodpilot/user/MainActivity.kt
// Replace its entire contents with this new, more robust version.

package com.diu.foodpilot.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.diu.foodpilot.user.screens.AuthScreen
import com.diu.foodpilot.user.ui.theme.FoodPilotDIUTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodPilotDIUTheme {
                // This state will now be updated automatically by our listener.
                var isLoggedIn by remember { mutableStateOf(Firebase.auth.currentUser != null) }

                // This is the standard way to use listeners in Compose.
                // It attaches the listener when the app starts and removes it when it closes.
                DisposableEffect(Unit) {
                    val auth = Firebase.auth
                    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        isLoggedIn = firebaseAuth.currentUser != null
                    }
                    auth.addAuthStateListener(listener)

                    // This cleans up the listener when the app is closed.
                    onDispose {
                        auth.removeAuthStateListener(listener)
                    }
                }

                if (isLoggedIn) {
                    // If logged in, show the main app screen
                    MainScreen()
                } else {
                    // If not logged in, show the authentication screen
                    AuthScreen(
                        onLoginSuccess = {
                            // We no longer need to do anything here.
                            // The listener will automatically handle the UI change.
                        }
                    )
                }
            }
        }
    }
}
