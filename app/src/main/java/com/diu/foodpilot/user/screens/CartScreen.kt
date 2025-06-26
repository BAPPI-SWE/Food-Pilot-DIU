// Open this file: app/src/main/java/com/diu/foodpilot/user/screens/CartScreen.kt
// Replace its entire contents with this modernized version.

@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diu.foodpilot.user.viewmodel.CartViewModel
import com.diu.foodpilot.user.viewmodel.RestaurantCart

@Composable
fun CartScreen(cartViewModel: CartViewModel) {
    val restaurantCarts by cartViewModel.restaurantCarts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        // THE FIX: Using the new modern background color from the theme
        val backgroundColor = MaterialTheme.colorScheme.surface

        if (restaurantCarts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Your cart is empty!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                restaurantCarts.values.forEach { restaurantCart ->
                    item {
                        RestaurantCartSection(
                            restaurantCart = restaurantCart,
                            onQuantityChange = { foodId, change ->
                                cartViewModel.updateItemQuantity(restaurantCart.restaurant.id, foodId, change)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantCartSection(
    restaurantCart: RestaurantCart,
    onQuantityChange: (foodId: String, change: Int) -> Unit
) {
    val totalPrice = restaurantCart.items.sumOf { it.price * it.quantity }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        // THE FIX: Using the new modern background color from the theme
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                restaurantCart.restaurant.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            restaurantCart.items.forEach { cartItem ->
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cartItem.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("BDT ${cartItem.price}", color = MaterialTheme.colorScheme.primary)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(onClick = { onQuantityChange(cartItem.foodId, -1) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Remove, "Remove one", tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("${cartItem.quantity}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        IconButton(onClick = { onQuantityChange(cartItem.foodId, 1) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Add, "Add one", tint = Color.White, modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).padding(4.dp))
                        }
                    }
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", style = MaterialTheme.typography.titleLarge)
                Text("BDT ${"%.2f".format(totalPrice)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: Place Order for this restaurant */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Place Order", fontSize = 18.sp)
            }
        }
    }
}
