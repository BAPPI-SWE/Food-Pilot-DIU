// Open this file: app/src/main/java/com/diu/foodpilot/user/screens/HomeScreen.kt
// Replace its entire contents with this version.

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.diu.foodpilot.user.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.diu.foodpilot.user.data.model.Offer
import com.diu.foodpilot.user.data.model.Restaurant
import com.diu.foodpilot.user.ui.theme.TextSecondary
import com.diu.foodpilot.user.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onRestaurantClick: (restaurantId: String, restaurantName: String) -> Unit
) {
    val restaurants by homeViewModel.restaurants.collectAsState()
    val offers by homeViewModel.offers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // We use a Scaffold without a topBar to get the correct background and padding.
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // This handles insets like the status bar
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Item 1: The new modern header
            item {
                HomeHeader(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            // Item 2: Offer Slider
            item {
                if (offers.isNotEmpty()) {
                    OfferSlider(offers = offers)
                }
            }

            // Item 3: "All Restaurants" title
            item {
                Text(
                    text = "All Restaurants",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            // The list of restaurants
            items(restaurants.filter { it.name.contains(searchQuery, ignoreCase = true) }) { restaurant ->
                ModernRestaurantCard(
                    restaurant = restaurant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = { onRestaurantClick(restaurant.id, restaurant.name) }
                )
            }
        }
    }
}

// The new, modern header composable
@Composable
fun HomeHeader(searchQuery: String, onQueryChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
    ) {
        // Top row: Deliver to & Notification Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("DELIVER TO", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Text("Daffodil Smart City, Ashulia", color = Color.White, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar, now integrated into the header
        TextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Search restaurants & food...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OfferSlider(offers: List<Offer>) {
    val pagerState = rememberPagerState(pageCount = { offers.size })

    LaunchedEffect(Unit) {
        while(true) {
            yield()
            delay(4000)
            if (pagerState.pageCount > 0) {
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % pagerState.pageCount
                )
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.height(180.dp),
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) { page ->
        Card(
            Modifier
                .graphicsLayer {
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                }
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = offers[page].imageUrl),
                contentDescription = "Offer",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}

@Composable
fun ModernRestaurantCard(restaurant: Restaurant, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = restaurant.imageUrl),
                contentDescription = restaurant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(140.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(restaurant.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${restaurant.rating} • ${restaurant.deliveryTime} • ${restaurant.cuisine}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("৳ ${restaurant.deliveryFee} Delivery Fee", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}
