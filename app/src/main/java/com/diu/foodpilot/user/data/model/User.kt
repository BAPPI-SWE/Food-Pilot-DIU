
// --- File 3: Update the existing User.kt file ---
package com.diu.foodpilot.user.data.model

// The Address data class is now more detailed
data class Address(
    val baseLocationName: String = "",
    val subLocation: String = "",
    val building: String = "",
    val floor: String = "",
    val room: String = ""
)

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val address: Address = Address()
)
