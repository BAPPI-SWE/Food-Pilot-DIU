
@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user.screens

import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diu.foodpilot.user.R
import com.diu.foodpilot.user.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    googleSignInLauncher: ManagedActivityResultLauncher<android.content.Intent, ActivityResult>
) {
    var showLogin by remember { mutableStateOf(true) }

    if (showLogin) {
        LoginScreen(
            onLoginSuccess = onLoginSuccess,
            onNavigateToSignUp = { showLogin = false },
            googleSignInLauncher = googleSignInLauncher
        )
    } else {
        SignUpScreen(
            onSignUpSuccess = onLoginSuccess,
            onNavigateToLogin = { showLogin = true }
        )
    }
}


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    googleSignInLauncher: ManagedActivityResultLauncher<android.content.Intent, ActivityResult>,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authResult by authViewModel.authResult.collectAsState()

    LaunchedEffect(authResult) {
        if (authResult == "Success") {
            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
            authViewModel.resetAuthResult()
            onLoginSuccess()
        } else if (authResult != null) {
            Toast.makeText(context, "Error: $authResult", Toast.LENGTH_LONG).show()
            authViewModel.resetAuthResult()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome Back!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Log in to continue", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { authViewModel.signIn(email, password) }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
            Text("Log In", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        GoogleSignInButton {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Don't have an account? Sign Up", modifier = Modifier.clickable(onClick = onNavigateToSignUp), color = MaterialTheme.colorScheme.primary)
    }
}

// THE FIX: The full implementation of SignUpScreen is now included.
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authResult by authViewModel.authResult.collectAsState()

    LaunchedEffect(authResult) {
        if (authResult == "Success") {
            Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
            authViewModel.resetAuthResult()
            onSignUpSuccess()
        } else if (authResult != null) {
            Toast.makeText(context, "Error: $authResult", Toast.LENGTH_LONG).show()
            authViewModel.resetAuthResult()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Get started with your new account", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (password == confirmPassword) {
                    authViewModel.signUp(email, password)
                } else {
                    Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text("Sign Up", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Already have an account? Log In",
            modifier = Modifier.clickable(onClick = onNavigateToLogin),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        Image(painter = painterResource(id = R.drawable.ic_google_logo), contentDescription = "Google Logo", modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text("Sign In with Google", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
