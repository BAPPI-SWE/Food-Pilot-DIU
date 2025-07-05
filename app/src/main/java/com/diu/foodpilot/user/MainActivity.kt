

package com.diu.foodpilot.user

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diu.foodpilot.user.screens.AuthScreen
import com.diu.foodpilot.user.screens.LocationSelectionScreen
import com.diu.foodpilot.user.ui.theme.FoodPilotDIUTheme
import com.diu.foodpilot.user.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodPilotDIUTheme {
                // This state now determines which of the three main flows to show
                var currentAppFlow by remember { mutableStateOf(AppFlow.LOADING) }
                val authViewModel: AuthViewModel = viewModel()

                // Check initial state when the app starts
                LaunchedEffect(Unit) {
                    val sharedPref = getSharedPreferences("user_location", Context.MODE_PRIVATE)
                    val locationId = sharedPref.getString("base_location_id", null)
                    currentAppFlow = if (locationId == null) {
                        AppFlow.LOCATION_SELECTION
                    } else {
                        if (Firebase.auth.currentUser != null) AppFlow.MAIN_APP else AppFlow.AUTHENTICATION
                    }
                }

                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)!!
                        account.idToken?.let { idToken ->
                            authViewModel.signInWithGoogle(idToken)
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(this, "Google Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }

                // Listen for authentication changes to switch flows
                DisposableEffect(Unit) {
                    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        if (currentAppFlow != AppFlow.LOCATION_SELECTION) {
                            currentAppFlow = if (firebaseAuth.currentUser != null) {
                                AppFlow.MAIN_APP
                            } else {
                                AppFlow.AUTHENTICATION
                            }
                        }
                    }
                    Firebase.auth.addAuthStateListener(listener)
                    onDispose { Firebase.auth.removeAuthStateListener(listener) }
                }

                when (currentAppFlow) {
                    AppFlow.LOADING -> { /* Show a loading screen or nothing */ }
                    AppFlow.LOCATION_SELECTION -> LocationSelectionScreen(
                        onLocationSelected = { currentAppFlow = AppFlow.AUTHENTICATION }
                    )
                    AppFlow.AUTHENTICATION -> AuthScreen(
                        onLoginSuccess = { currentAppFlow = AppFlow.MAIN_APP },
                        googleSignInLauncher = googleSignInLauncher
                    )
                    AppFlow.MAIN_APP -> MainScreen()
                }
            }
        }
    }
}

// Helper enum to manage the app's state
private enum class AppFlow {
    LOADING,
    LOCATION_SELECTION,
    AUTHENTICATION,
    MAIN_APP
}
