// Open this file:
// app/src/main/java/com/diu/foodpilot/user/viewmodel/ProfileViewModel.kt
// Replace its entire contents with this new version.

package com.diu.foodpilot.user.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ProfileViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    fun loadInitialImage(context: Context) {
        // ... (this function is unchanged)
    }

    fun onImageSelected(context: Context, uri: Uri?) {
        // ... (this function is unchanged)
    }

    // THE FIX: We remove fetchUserProfile() from the init block.
    // It will now be called from the UI.
    init {
        // The init block is now empty.
    }

    fun fetchUserProfile() {
        // THE FIX: We get the current user ID every time this function is called.
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("ProfileViewModel", "Cannot fetch profile, user is not logged in.")
            // Set a default user object to stop the loading screen
            _user.value = User()
            return
        }

        viewModelScope.launch {
            db.collection("users").document(userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("ProfileViewModel", "Listen failed.", e)
                        _user.value = User(uid = userId)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        _user.value = snapshot.toObject(User::class.java)
                    } else {
                        _user.value = User(uid = userId, email = auth.currentUser?.email ?: "")
                    }
                }
        }
    }

    fun saveUserProfile(name: String, phone: String, location: String, floor: String, room: String) {
        // ... (this function is unchanged)
    }
}
