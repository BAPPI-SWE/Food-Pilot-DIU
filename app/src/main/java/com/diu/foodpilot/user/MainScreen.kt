// Open this file: app/src/main/java/com/diu/foodpilot/user/MainScreen.kt
// Replace its entire contents with this new, corrected version.

@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.diu.foodpilot.user.navigation.NavigationItem
import com.diu.foodpilot.user.screens.*
import com.diu.foodpilot.user.ui.theme.PrimaryRed
import com.diu.foodpilot.user.viewmodel.CartViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(
                    NavigationItem.Home,
                    NavigationItem.Cart,
                    NavigationItem.Orders,
                    NavigationItem.Profile
                )
                items.forEach { item ->
                    // THE FIX: This logic correctly determines if a screen in the hierarchy is selected.
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
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
        NavHost(
            navController,
            startDestination = NavigationItem.Home.route,
            // Apply only the bottom padding from the Scaffold
            modifier = Modifier.padding(PaddingValues(bottom = innerPadding.calculateBottomPadding()))
        ) {
            // THE FIX: Create a nested graph for the "Home" tab's flow
            navigation(
                startDestination = "home_screen",
                route = NavigationItem.Home.route
            ) {
                composable("home_screen") {
                    HomeScreen(onRestaurantClick = { restaurantId, restaurantName ->
                        val encodedName = URLEncoder.encode(restaurantName, StandardCharsets.UTF_8.toString())
                        navController.navigate("restaurant_menu/$restaurantId/$encodedName")
                    })
                }
                composable(
                    "restaurant_menu/{restaurantId}/{restaurantName}",
                    arguments = listOf(
                        navArgument("restaurantId") { type = NavType.StringType },
                        navArgument("restaurantName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val restaurantName = backStackEntry.arguments?.getString("restaurantName")?.let {
                        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                    }
                    RestaurantMenuScreen(
                        restaurantName = restaurantName,
                        onNavigateBack = { navController.popBackStack() },
                        onAddToCart = { restaurant, selection ->
                            cartViewModel.addSelectionToCart(restaurant, selection)
                        },
                        onPlaceOrder = { restaurant, selection ->
                            cartViewModel.addSelectionToCart(restaurant, selection)
                            navController.navigate(NavigationItem.Cart.route)
                        }
                    )
                }
            }

            // Other top-level destinations
            composable(NavigationItem.Cart.route) { CartScreen(cartViewModel = cartViewModel) }
            composable(NavigationItem.Orders.route) { OrdersScreen() }
            composable(NavigationItem.Profile.route) { ProfileScreen() }
        }
    }
}
