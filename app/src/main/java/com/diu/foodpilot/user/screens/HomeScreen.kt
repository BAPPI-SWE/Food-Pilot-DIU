// Open this file: app/src/main/java/com/diu/foodpilot/user/screens/HomeScreen.kt
// Replace its entire contents with this version.

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val isRefreshing by homeViewModel.isRefreshing.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { homeViewModel.refresh() }
    )

    // The main layout is now a Box to contain the refresh indicator
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .pullRefresh(pullRefreshState)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    if (offers.isNotEmpty()) {
                        OfferSlider(offers = offers)
                    }
                }
                item {
                    Text(
                        text = "All Restaurants",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                    )
                }
                items(restaurants.filter { it.name.contains(searchQuery, ignoreCase = true) }) { restaurant ->
                    ModernRestaurantCard(
                        restaurant = restaurant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        onClick = { onRestaurantClick(restaurant.id, restaurant.name) }
                    )
                }
            }
        }

        // The circular progress indicator
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            // Move indicator below the header
            scale = true,
            contentColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surface
        )
    }
}

// HomeHeader, OfferSlider, and ModernRestaurantCard functions remain the same...
@Composable
fun HomeHeader(searchQuery: String, onQueryChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
    ) {
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
