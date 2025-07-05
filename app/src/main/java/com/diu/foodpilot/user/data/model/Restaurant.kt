package com.diu.foodpilot.user.data.model

import com.google.firebase.firestore.PropertyName

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val isOpen: Boolean = true,
    val rating: Double = 0.0,
    val deliveryTime: String = "20-30 min",
    val deliveryFee: String = "25", // Keeping as String as per your last fix
    val cuisine: String = "Fast Food",

    // This is the major change
    val supportedLocations: Map<String, List<String>> = emptyMap(),

    @get:PropertyName("isInstantDeliveryAvailable")
    val isInstantDeliveryAvailable: Boolean = false
)

