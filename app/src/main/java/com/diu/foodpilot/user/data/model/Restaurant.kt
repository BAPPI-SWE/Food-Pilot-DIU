

package com.diu.foodpilot.user.data.model

// 1. Add this import statement at the top.
import com.google.firebase.firestore.PropertyName

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val isOpen: Boolean = true,

    // 2. Add this annotation right above the property.
    // This explicitly tells the app to look for the field "isInstantDeliveryAvailable"
    // in the Firestore document.
    @get:PropertyName("isInstantDeliveryAvailable")
    val isInstantDeliveryAvailable: Boolean = false
)
