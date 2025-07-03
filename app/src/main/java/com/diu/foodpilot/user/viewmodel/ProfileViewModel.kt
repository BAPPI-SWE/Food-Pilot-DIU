// Open this file:
// app/src/main/java/com/diu/foodpilot/user/viewmodel/ProfileViewModel.kt
// Replace its entire contents with this new version.

package com.diu.foodpilot.user.viewmodel

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

class ProfileViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    // --- NEW: State to hold the selected image URI in the ViewModel ---
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    init {
        fetchUserProfile()
    }

    // --- NEW: Function to update the selected image URI ---
    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    private fun fetchUserProfile() {
        if (userId == null) {
            Log.e("ProfileViewModel", "Cannot fetch profile, user is not logged in.")
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
        if (userId == null) return
        viewModelScope.launch {
            val userProfile = _user.value?.copy(
                name = name,
                phone = phone,
                address = _user.value!!.address.copy(
                    location = location,
                    floor = floor,
                    room = room
                )
            ) ?: return@launch

            db.collection("users").document(userId)
                .set(userProfile)
                .addOnSuccessListener { Log.d("ProfileViewModel", "User profile successfully written!") }
                .addOnFailureListener { e -> Log.w("ProfileViewModel", "Error writing document", e) }
        }
    }
}
