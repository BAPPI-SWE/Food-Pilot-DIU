
package com.diu.foodpilot.user.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.Location
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations = _locations.asStateFlow()

    init {
        fetchLocations()
    }

    private fun fetchLocations() {
        viewModelScope.launch {
            db.collection("locations")
                .orderBy("order", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // Handle error
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val locationList = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Location::class.java)?.copy(id = doc.id)
                        }
                        _locations.value = locationList
                    }
                }
        }
    }

    fun saveUserLocation(context: Context, baseLocationId: String, baseLocationName: String, subLocation: String) {
        val sharedPref = context.getSharedPreferences("user_location", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("base_location_id", baseLocationId)
            putString("base_location_name", baseLocationName)
            putString("sub_location", subLocation)
            apply()
        }
    }
}
