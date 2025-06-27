
package com.diu.foodpilot.user.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.Offer // <-- Add this import
import com.diu.foodpilot.user.data.model.Restaurant
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val db = Firebase.firestore

    // --- Restaurants ---
    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants.asStateFlow()

    // --- NEW: Offers ---
    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    init {
        fetchRestaurants()
        fetchOffers() // <-- Call the new function
    }

    // --- NEW: Function to fetch offers from Firestore ---
    private fun fetchOffers() {
        viewModelScope.launch {
            db.collection("offers")
                .orderBy("order", Query.Direction.ASCENDING) // Order slides by the 'order' field
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("HomeViewModel", "Offer listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        _offers.value = snapshot.toObjects(Offer::class.java)
                    }
                }
        }
    }

    private fun fetchRestaurants() {
        viewModelScope.launch {
            db.collection("restaurants")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("HomeViewModel", "Restaurant listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val restaurantList = snapshot.documents.mapNotNull {
                            val restaurant = it.toObject(Restaurant::class.java)
                            restaurant?.copy(id = it.id)
                        }
                        _restaurants.value = restaurantList
                    } else {
                        Log.d("HomeViewModel", "Restaurant data: null")
                    }
                }
        }
    }
}
