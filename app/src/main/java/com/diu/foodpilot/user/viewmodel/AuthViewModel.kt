
package com.diu.foodpilot.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    val currentUser = auth.currentUser

    private val _authResult = MutableStateFlow<String?>(null)
    val authResult = _authResult.asStateFlow()

    // --- NEW: Function to handle Google Sign-In ---
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _authResult.value = "Success"
            } catch (e: Exception) {
                _authResult.value = e.message
            }
        }
    }

    fun signUp(email: String, pass: String) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, pass).await()
                _authResult.value = "Success"
            } catch (e: Exception) {
                _authResult.value = e.message
            }
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                _authResult.value = "Success"
            } catch (e: Exception) {
                _authResult.value = e.message
            }
        }
    }

    fun resetAuthResult() {
        _authResult.value = null
    }
}

