

package com.diu.foodpilot.user.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

// This data class is used inside the Order
data class CartItem(
    val foodId: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0
)

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val restaurantId: String = "",
    // THE FIX: Changed the type to 'Any' to accept both Timestamp and FieldValue
    val orderTimestamp: Any = FieldValue.serverTimestamp(),
    val deliveryAddress: Address = Address(),
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "pending"
)
