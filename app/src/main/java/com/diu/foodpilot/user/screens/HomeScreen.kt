
// --- File 2: HomeScreen.kt (UPDATED) ---
// Open this file and replace its contents. We are adding the 'onRestaurantClick' parameter.

@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.diu.foodpilot.user.data.model.Restaurant
import com.diu.foodpilot.user.ui.theme.LightPink
import com.diu.foodpilot.user.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onRestaurantClick: (restaurantId: String, restaurantName: String) -> Unit // New parameter
) {
    val restaurants by homeViewModel.restaurants.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Pilot DIU") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightPink)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Find Restaurants") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(restaurants.filter { it.name.contains(searchQuery, ignoreCase = true) }) { restaurant ->
                    RestaurantCard(restaurant = restaurant) {
                        // Pass the id and name to the lambda when clicked
                        onRestaurantClick(restaurant.id, restaurant.name)
                    }
                }
            }
        }
    }
}

// A reusable composable for displaying a single restaurant card
@Composable
fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    // Generate a unique, stable color based on the restaurant's name hash code
    val cardColors = listOf(
        Color(0xFFFCE4EC), Color(0xFFF3E5F5), Color(0xFFE8EAF6),
        Color(0xFFE3F2FD), Color(0xFFE0F7FA), Color(0xFFE8F5E9)
    )
    val cardColor = remember(restaurant.id) {
        cardColors[java.lang.Math.abs(restaurant.id.hashCode()) % cardColors.size]
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(180.dp)
                .background(cardColor)
        ) {
            // Background Image
            Image(
                painter = rememberAsyncImagePainter(restaurant.imageUrl),
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )

            // Restaurant Name
            Text(
                text = restaurant.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
    }
}
