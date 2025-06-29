

package com.diu.foodpilot.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    // Exposes the current logged-in user or null if not logged in
    val currentUser = auth.currentUser

    // A flow to communicate the result of an auth action (success or error message)
    private val _authResult = MutableStateFlow<String?>(null)
    val authResult = _authResult.asStateFlow()

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
