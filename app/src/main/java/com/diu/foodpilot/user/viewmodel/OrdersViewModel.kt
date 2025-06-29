

package com.diu.foodpilot.user.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.Order
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val userId = auth.currentUser?.uid

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        if (userId == null) {
            Log.e("OrdersViewModel", "User not logged in, cannot fetch orders.")
            return
        }

        viewModelScope.launch {
            db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("orderTimestamp", Query.Direction.DESCENDING) // Show newest orders first
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("OrdersViewModel", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val orderList = snapshot.toObjects(Order::class.java)
                        // We need to manually add the document ID to our order object
                        val ordersWithIds = snapshot.documents.mapIndexed { index, document ->
                            orderList[index].copy(orderId = document.id)
                        }
                        _orders.value = ordersWithIds
                    }
                }
        }
    }
}
