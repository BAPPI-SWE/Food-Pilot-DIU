package com.diu.foodpilot.user.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
// Import the AutoMirrored version of the List icon
import androidx.compose.material.icons.automirrored.filled.List // Changed import
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem("home", Icons.Filled.Home, "Home")
    object Cart : NavigationItem("cart", Icons.Filled.ShoppingCart, "Cart")
    // Use the AutoMirrored version of the List icon
    object Orders : NavigationItem("orders", Icons.AutoMirrored.Filled.List, "Orders") // Changed usage
    object Profile : NavigationItem("profile", Icons.Filled.Person, "Profile")
}