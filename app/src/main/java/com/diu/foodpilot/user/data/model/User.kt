package com.diu.foodpilot.user.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String? = null,
    val address: Address = Address()
)

data class Address(
    val location: String = "Hall 1", // Can be "Hall 1", "Hall 2", "Faculty Room"
    val floor: String = "",
    val room: String = ""
)
