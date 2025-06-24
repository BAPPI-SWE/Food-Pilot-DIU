
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
import androidx.lifecycle.viewmodel.compose.viewModel
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
                        val encodedName = URLEncoder.encode(restaurantName, StandardCharsets.UTF_8.toString())
                        navController.navigate("restaurant_menu/$restaurantId/$encodedName")
                    })
                }
                composable(NavigationItem.Cart.route) {
                    // Pass the cartViewModel to the CartScreen
                    CartScreen(cartViewModel = cartViewModel)
                }
                composable(NavigationItem.Orders.route) { OrdersScreen() }
                composable(NavigationItem.Profile.route) { ProfileScreen() }

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
                        cartViewModel = cartViewModel,
                        // THE NEW LOGIC: This now calls the method in our CartViewModel
                        onAddToCart = { restaurant, selection ->
                            cartViewModel.addSelectionToCart(restaurant, selection)
                        }
                    )
                }
            }
        }
    }
}
