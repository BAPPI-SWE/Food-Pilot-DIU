package com.diu.foodpilot.user.data.model


import com.google.firebase.Timestamp

// This data class will be used inside the Order
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
    val orderTimestamp: Timestamp = Timestamp.now(),
    val deliveryAddress: Address = Address(),
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "pending" // "pending", "confirmed", "preparing", "out_for_delivery", "delivered", "cancelled"
)
