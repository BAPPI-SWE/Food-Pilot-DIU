// Open this file: app/src/main/java/com/diu/foodpilot/user/screens/RestaurantMenuScreen.kt
// Replace its entire contents with this modernized version.

@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.diu.foodpilot.user.data.model.CartItem
import com.diu.foodpilot.user.data.model.FoodItem
import com.diu.foodpilot.user.data.model.Restaurant
import com.diu.foodpilot.user.viewmodel.CartViewModel
import com.diu.foodpilot.user.viewmodel.RestaurantMenuViewModel

@Composable
fun RestaurantMenuScreen(
    restaurantName: String?,
    onNavigateBack: () -> Unit,
    onAddToCart: (Restaurant, Map<String, CartItem>) -> Unit,
    onPlaceOrder: (Restaurant, Map<String, CartItem>) -> Unit,
    menuViewModel: RestaurantMenuViewModel = viewModel()
) {
    val restaurantDetails by menuViewModel.restaurantDetails.collectAsState()
    val temporarySelection by menuViewModel.temporarySelection.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurantName ?: "Menu") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        bottomBar = {
            AnimatedVisibility(visible = temporarySelection.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            restaurantDetails?.let {
                                onAddToCart(it, temporarySelection)
                                menuViewModel.clearTemporarySelection()
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = "Add to Cart Icon", tint = MaterialTheme.colorScheme.primary)
                    }

                    Button(
                        onClick = {
                            restaurantDetails?.let {
                                onPlaceOrder(it, temporarySelection)
                                menuViewModel.clearTemporarySelection()
                            }
                        },
                        modifier = Modifier.weight(2.5f), // Made button wider
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Place Order Icon")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Place Order Now", fontSize = 18.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (restaurantDetails == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val isInstantDeliveryAvailable = restaurantDetails!!.isInstantDeliveryAvailable
            val currentMenu by menuViewModel.currentMenu.collectAsState()
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Typography and spelling fixes applied here
                item { Text("Pre-Order (Always Available)", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)) }
                item { ModernPreOrderCard(title = "Breakfast", time = "Order: 7am-10am", delivery = "Delivery: 10:30am") }
                item { ModernPreOrderCard(title = "Lunch", time = "Order: 10am-12pm", delivery = "Delivery: 1:30pm") } // "Lunch" corrected
                item { ModernPreOrderCard(title = "Dinner", time = "Order: 4pm-6pm", delivery = "Delivery: 8:00pm") }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Menu", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                        DeliveryStatusIndicator(isAvailable = isInstantDeliveryAvailable)
                    }
                }

                items(currentMenu) { foodItem ->
                    val quantity = temporarySelection[foodItem.id]?.quantity ?: 0
                    ModernFoodItemCard(
                        foodItem = foodItem,
                        quantity = quantity,
                        enabled = isInstantDeliveryAvailable,
                        onQuantityChange = { change ->
                            menuViewModel.updateTemporarySelection(foodItem, change)
                        }
                    )
                }
            }
        }
    }
}

// Re-using the same modern indicator from the HomeScreen
@Composable
fun DeliveryStatusIndicator(isAvailable: Boolean) {
    val backgroundColor = if (isAvailable) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
    val textColor = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828)
    val text = if (isAvailable) "Available" else "Unavailable"

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(text, color = textColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

// A slightly more modern take on the PreOrderCard
@Composable
fun ModernPreOrderCard(title: String, time: String, delivery: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold))
                Text(time, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(delivery, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Icon(Icons.Default.Add, "Order " + title, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
        }
    }
}

// Modernized FoodItemCard
@Composable
fun ModernFoodItemCard(foodItem: FoodItem, quantity: Int, enabled: Boolean, onQuantityChange: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = foodItem.imageUrl),
                contentDescription = foodItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(foodItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("BDT ${foodItem.price}", color = MaterialTheme.colorScheme.primary)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (quantity > 0) {
                    IconButton(onClick = { onQuantityChange(-1) }, enabled = enabled, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, "Remove one", tint = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray)
                    }
                    Text("$quantity", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (enabled) Color.Unspecified else Color.Gray)
                }
                IconButton(onClick = { onQuantityChange(1) }, enabled = enabled, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, "Add one", tint = if (enabled) Color.White else Color.LightGray, modifier = Modifier.background(if (enabled) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape).padding(4.dp))
                }
            }
        }
    }
}
