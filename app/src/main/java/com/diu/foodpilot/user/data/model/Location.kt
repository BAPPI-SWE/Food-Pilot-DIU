package com.diu.foodpilot.user.data.model

data class Location(
    val id: String = "", // The document ID from Firestore
    val name: String = "",
    val subLocations: List<String> = emptyList(),
    val order: Int = 0
)
