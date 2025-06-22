// Open this file: app/src/main/java/com/diu/foodpilot/user/screens/RestaurantMenuScreen.kt
// Replace the ENTIRE content of the file with this new version.

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
// We are now importing a different icon set to avoid the error
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.diu.foodpilot.user.data.model.FoodItem
import com.diu.foodpilot.user.ui.theme.LightPink
import com.diu.foodpilot.user.viewmodel.CartViewModel
import com.diu.foodpilot.user.viewmodel.RestaurantMenuViewModel

@Composable
fun RestaurantMenuScreen(
    restaurantName: String?,
    onNavigateBack: () -> Unit,
    menuViewModel: RestaurantMenuViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel() // Add CartViewModel
) {
    val currentMenu by menuViewModel.currentMenu.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurantName ?: "Menu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(visible = cartItems.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { /* TODO: Navigate to Cart Screen */ }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart Icon")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Cart (${cartItems.sumOf { it.quantity }})")
                    }
                    Button(onClick = { /* TODO: Place Order Logic */ }) {
                        Text("Place Order")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightPink),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Text("Pre-Order", style = MaterialTheme.typography.headlineMedium) }
            item { PreOrderCard(title = "Breakfast", time = "Order: 7am-10am", delivery = "Delivery: 10:30am") }
            item { PreOrderCard(title = "Launch", time = "Order: 10am-12pm", delivery = "Delivery: 1:30pm") }
            item { PreOrderCard(title = "Dinner", time = "Order: 4pm-6pm", delivery = "Delivery: 8:00pm") }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Current Menu", style = MaterialTheme.typography.headlineMedium)
            }
            items(currentMenu) { foodItem ->
                val cartItem = cartItems.find { it.foodId == foodItem.id }
                FoodItemCard(
                    foodItem = foodItem,
                    quantity = cartItem?.quantity ?: 0,
                    onAddItem = { cartViewModel.addItem(foodItem) },
                    onRemoveItem = { cartViewModel.removeItem(foodItem) }
                )
            }
        }
    }
}

@Composable
fun PreOrderCard(title: String, time: String, delivery: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(delivery, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Order " + title,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    quantity: Int,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = foodItem.imageUrl),
                contentDescription = foodItem.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(foodItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Price: ${foodItem.price} BDT", color = MaterialTheme.colorScheme.primary)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (quantity > 0) {
                    IconButton(onClick = onRemoveItem, modifier = Modifier.size(32.dp)) {
                        Icon(
                            // THE FIX: Using a different icon from a different package
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = "Remove Item",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(text = "$quantity", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                IconButton(onClick = onAddItem, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Item",
                        tint = Color.White,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
