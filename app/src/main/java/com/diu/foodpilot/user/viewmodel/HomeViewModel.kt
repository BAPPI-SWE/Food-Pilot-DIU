package com.diu.foodpilot.user.viewmodel




import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.Restaurant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// A ViewModel's job is to provide data to the UI and survive configuration changes.
// This ViewModel will fetch and hold our list of restaurants.
class HomeViewModel : ViewModel() {

    // Get a reference to the Firestore database
    private val db = Firebase.firestore

    // This is a private, mutable state flow.
    // We will update this with the list of restaurants from Firestore.
    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())

    // This is the public, read-only state flow that the UI will observe.
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    // init block is called when the ViewModel is created.
    init {
        fetchRestaurants()
    }

    private fun fetchRestaurants() {
        // We use viewModelScope.launch to start a coroutine that will
        // live as long as the ViewModel.
        viewModelScope.launch {
            db.collection("restaurants")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("HomeViewModel", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        // Map the documents to our Restaurant data class
                        val restaurantList = snapshot.documents.mapNotNull {
                            val restaurant = it.toObject(Restaurant::class.java)
                            // We set the document ID here
                            restaurant?.copy(id = it.id)
                        }
                        _restaurants.value = restaurantList
                    } else {
                        Log.d("HomeViewModel", "Current data: null")
                    }
                }
        }
    }
}
