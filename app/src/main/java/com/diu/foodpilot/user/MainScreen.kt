// --- File 1: MainScreen.kt (UPDATED) ---
// Open this file again: com.diu.foodpilot.user/MainScreen.kt
// Replace all the code with this updated version to include the new navigation route.
@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.diu.foodpilot.user.navigation.NavigationItem
import com.diu.foodpilot.user.screens.*
import com.diu.foodpilot.user.ui.theme.PrimaryRed
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            // The bottom bar code is the same as before
            NavigationBar(
                containerColor = Color.White
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val items = listOf(
                    NavigationItem.Home,
                    NavigationItem.Cart,
                    NavigationItem.Orders,
                    NavigationItem.Profile
                )

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute?.startsWith(item.route) == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(text = item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryRed,
                            selectedTextColor = PrimaryRed,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController, startDestination = NavigationItem.Home.route) {
                composable(NavigationItem.Home.route) {
                    HomeScreen(onRestaurantClick = { restaurantId, restaurantName ->
                        // URL-encode the name to handle special characters
                        val encodedName = URLEncoder.encode(restaurantName, StandardCharsets.UTF_8.toString())
                        navController.navigate("restaurant_menu/$restaurantId/$encodedName")
                    })
                }
                composable(NavigationItem.Cart.route) { CartScreen() }
                composable(NavigationItem.Orders.route) { OrdersScreen() }
                composable(NavigationItem.Profile.route) { ProfileScreen() }

                // New Route for the Menu Screen
                composable(
                    "restaurant_menu/{restaurantId}/{restaurantName}",
                    arguments = listOf(
                        navArgument("restaurantId") { type = NavType.StringType },
                        navArgument("restaurantName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val restaurantId = backStackEntry.arguments?.getString("restaurantId")
                    val restaurantName = backStackEntry.arguments?.getString("restaurantName")?.let {
                        // Decode the name back to its original form
                        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                    }
                    RestaurantMenuScreen(
                        restaurantName = restaurantName,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
