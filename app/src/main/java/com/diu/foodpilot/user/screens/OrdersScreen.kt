

@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diu.foodpilot.user.data.model.Order
import com.diu.foodpilot.user.viewmodel.OrdersViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(ordersViewModel: OrdersViewModel = viewModel()) {
    val orders by ordersViewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        val backgroundColor = MaterialTheme.colorScheme.surface
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues).background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "You haven't placed any orders yet.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).background(backgroundColor),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderId.take(6)}...", // Show a short version of the ID
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(status = order.status)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Order Details
            OrderDetailRow(label = "Date", value = formatTimestamp(order.orderTimestamp))
            OrderDetailRow(label = "Total Items", value = order.items.sumOf { it.quantity }.toString())
            OrderDetailRow(label = "Total Price", value = "BDT ${"%.2f".format(order.totalPrice)}", isHighlight = true)

        }
    }
}

@Composable
fun OrderDetailRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else Color.Unspecified
        )
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "pending" -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        "confirmed" -> Color(0xFFC8E6C9) to Color(0xFF2E7D32)
        "delivered" -> Color(0xFFB3E5FC) to Color(0xFF01579B)
        "cancelled" -> Color(0xFFFFCDD2) to Color(0xFFC62828)
        else -> Color.LightGray to Color.Black
    }

    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = backgroundColor)) {
        Text(
            text = status,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

fun formatTimestamp(timestamp: Any?): String {
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = (timestamp as Timestamp).toDate()
        sdf.format(date)
    } catch (e: Exception) {
        "Invalid Date"
    }
}
