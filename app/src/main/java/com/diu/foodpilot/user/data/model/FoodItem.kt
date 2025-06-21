package com.diu.foodpilot.user.data.model


// We use this to identify which menu an item belongs to
enum class FoodCategory {
    PREORDER_BREAKFAST,
    PREORDER_LUNCH,
    PREORDER_DINNER,
    CURRENT,
    UNKNOWN
}

data class FoodItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = FoodCategory.CURRENT.name, // Stored as a string in Firestore
    val isAvailable: Boolean = true,
    val restaurantId: String = "" // To know which restaurant it belongs to
)

