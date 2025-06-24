

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
import androidx.compose.material.icons.filled.Remove
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
import com.diu.foodpilot.user.ui.theme.LightPink
import com.diu.foodpilot.user.viewmodel.CartViewModel
import com.diu.foodpilot.user.viewmodel.RestaurantMenuViewModel

@Composable
fun RestaurantMenuScreen(
    restaurantName: String?,
    onNavigateBack: () -> Unit,
    cartViewModel: CartViewModel,
    onAddToCart: (Restaurant, Map<String, CartItem>) -> Unit,
    menuViewModel: RestaurantMenuViewModel = viewModel()
) {
    val currentMenu by menuViewModel.currentMenu.collectAsState()
    val restaurantDetails by menuViewModel.restaurantDetails.collectAsState()
    val temporarySelection by menuViewModel.temporarySelection.collectAsState()

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
            AnimatedVisibility(visible = temporarySelection.isNotEmpty()) {
                Button(
                    onClick = {
                        restaurantDetails?.let {
                            onAddToCart(it, temporarySelection)
                            menuViewModel.clearTemporarySelection()
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("Add To Cart", fontSize = 18.sp)
                }
            }
        }
    ) { paddingValues ->
        // THE FIX: Show a loading indicator until the restaurant details have been fetched.
        if (restaurantDetails == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val isInstantDeliveryAvailable = restaurantDetails!!.isInstantDeliveryAvailable
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(LightPink),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Text("Pre-Order (Always Available)", style = MaterialTheme.typography.headlineSmall) }
                item { PreOrderCard(title = "Breakfast", time = "Order: 7am-10am", delivery = "Delivery: 10:30am") }
                item { PreOrderCard(title = "Launch", time = "Order: 10am-12pm", delivery = "Delivery: 1:30pm") }
                item { PreOrderCard(title = "Dinner", time = "Order: 4pm-6pm", delivery = "Delivery: 8:00pm") }

                // UI CHANGE: The indicator is now inside a Row with the title.
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Menu", style = MaterialTheme.typography.headlineSmall)
                        DeliveryStatusIndicator(isAvailable = isInstantDeliveryAvailable)
                    }
                }

                items(currentMenu) { foodItem ->
                    val quantity = temporarySelection[foodItem.id]?.quantity ?: 0
                    FoodItemCard(
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

@Composable
fun DeliveryStatusIndicator(isAvailable: Boolean) {
    val backgroundColor = if (isAvailable) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
    val textColor = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828)
    val text = if (isAvailable) "Available" else "Unavailable"

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// PreOrderCard is now interactive
@Composable
fun PreOrderCard(
    title: String,
    time: String,
    delivery: String
) {
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
                imageVector = Icons.Default.Add,
                contentDescription = "Order " + title,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// FoodItemCard is completely reworked
@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    quantity: Int,
    enabled: Boolean,
    onQuantityChange: (Int) -> Unit
) {
    val contentColor = if (enabled) Color.Unspecified else Color.Gray
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = if (enabled) 1f else 0.6f)
        )
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
                    .clip(RoundedCornerShape(8.dp)),
                alpha = if (enabled) 1f else 0.5f
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(foodItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = contentColor)
                Text("Price: ${foodItem.price} BDT", color = MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.5f))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (quantity > 0) {
                    IconButton(
                        onClick = { onQuantityChange(-1) },
                        enabled = enabled,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove one",
                            tint = if(enabled) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    Text(text = "$quantity", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = contentColor)
                }
                IconButton(
                    onClick = { onQuantityChange(1) },
                    enabled = enabled,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add one",
                        tint = if (enabled) Color.White else Color.LightGray,
                        modifier = Modifier
                            .background(if (enabled) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
