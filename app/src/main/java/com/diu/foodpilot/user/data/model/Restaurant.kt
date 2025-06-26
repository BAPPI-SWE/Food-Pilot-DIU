
package com.diu.foodpilot.user.data.model

import com.google.firebase.firestore.PropertyName

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val isOpen: Boolean = true,

    // --- NEW FIELDS for Modern UI ---
    val rating: Double = 0.0,
    val deliveryTime: String = "20-30 min", // e.g., "20-30 min"
    val deliveryFee: String = "2" ,// e.g., 25
    val cuisine: String = "Fast Food", // e.g., "Biryani", "Chinese"

    @get:PropertyName("isInstantDeliveryAvailable")
    val isInstantDeliveryAvailable: Boolean = false
)
