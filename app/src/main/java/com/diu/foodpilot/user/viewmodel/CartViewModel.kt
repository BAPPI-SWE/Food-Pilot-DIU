// Open this file:
// app/src/main/java/com/diu/foodpilot/user/viewmodel/CartViewModel.kt
// Replace its entire contents with this new version.

package com.diu.foodpilot.user.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.Address
import com.diu.foodpilot.user.data.model.Order
import com.diu.foodpilot.user.data.model.Restaurant
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RestaurantCart(
    val restaurant: Restaurant,
    val items: List<com.diu.foodpilot.user.data.model.CartItem>
)

class CartViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _restaurantCarts = MutableStateFlow<Map<String, RestaurantCart>>(emptyMap())
    val restaurantCarts: StateFlow<Map<String, RestaurantCart>> = _restaurantCarts.asStateFlow()

    // A flow to communicate the result of placing an order
    private val _orderResult = MutableStateFlow<String?>(null)
    val orderResult = _orderResult.asStateFlow()

    fun addSelectionToCart(restaurant: Restaurant, selection: Map<String, com.diu.foodpilot.user.data.model.CartItem>) {
        if (selection.isEmpty()) return
        _restaurantCarts.update { currentCarts ->
            val mutableCarts = currentCarts.toMutableMap()
            val existingCart = mutableCarts[restaurant.id]
            val itemsToAdd = selection.values.toList()
            if (existingCart != null) {
                val updatedItems = existingCart.items.toMutableList()
                itemsToAdd.forEach { newItem ->
                    val existingItemIndex = updatedItems.indexOfFirst { it.foodId == newItem.foodId }
                    if (existingItemIndex != -1) {
                        val oldItem = updatedItems[existingItemIndex]
                        updatedItems[existingItemIndex] = oldItem.copy(quantity = oldItem.quantity + newItem.quantity)
                    } else {
                        updatedItems.add(newItem)
                    }
                }
                mutableCarts[restaurant.id] = existingCart.copy(items = updatedItems)
            } else {
                mutableCarts[restaurant.id] = RestaurantCart(restaurant, itemsToAdd)
            }
            mutableCarts
        }
    }

    fun updateItemQuantity(restaurantId: String, foodId: String, change: Int) {
        _restaurantCarts.update { currentCarts ->
            val mutableCarts = currentCarts.toMutableMap()
            val targetCart = mutableCarts[restaurantId] ?: return@update currentCarts
            val updatedItems = targetCart.items.toMutableList()
            val itemIndex = updatedItems.indexOfFirst { it.foodId == foodId }
            if (itemIndex == -1) return@update currentCarts
            val item = updatedItems[itemIndex]
            val newQuantity = item.quantity + change
            if (newQuantity > 0) {
                updatedItems[itemIndex] = item.copy(quantity = newQuantity)
            } else {
                updatedItems.removeAt(itemIndex)
            }
            if (updatedItems.isEmpty()) {
                mutableCarts.remove(restaurantId)
            } else {
                mutableCarts[restaurantId] = targetCart.copy(items = updatedItems)
            }
            mutableCarts
        }
    }

    // --- NEW FUNCTION TO PLACE AN ORDER ---
    fun placeOrder(restaurantId: String) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: run {
                _orderResult.value = "Error: User not logged in."
                return@launch
            }
            val cartToOrder = _restaurantCarts.value[restaurantId] ?: run {
                _orderResult.value = "Error: Cart not found."
                return@launch
            }

            try {
                // 1. Fetch the user's current address from their profile
                val userDoc = db.collection("users").document(userId).get().await()
                val userAddress = userDoc.toObject(com.diu.foodpilot.user.data.model.User::class.java)?.address ?: Address()

                // 2. Create the Order object
                val newOrder = Order(
                    userId = userId,
                    restaurantId = cartToOrder.restaurant.id,

                    orderTimestamp = FieldValue.serverTimestamp(), // Use server time for accuracy
                    deliveryAddress = userAddress,
                    items = cartToOrder.items,
                    totalPrice = cartToOrder.items.sumOf { it.price * it.quantity },
                    status = "Pending"
                )

                // 3. Save the order to the 'orders' collection
                db.collection("orders").add(newOrder).await()

                // 4. Clear the placed order from the cart
                clearCartForRestaurant(restaurantId)
                _orderResult.value = "Success: Order placed successfully!"

            } catch (e: Exception) {
                Log.e("CartViewModel", "Error placing order", e)
                _orderResult.value = "Error: ${e.message}"
            }
        }
    }

    private fun clearCartForRestaurant(restaurantId: String) {
        _restaurantCarts.update { currentCarts ->
            currentCarts.toMutableMap().apply {
                remove(restaurantId)
            }
        }
    }

    fun resetOrderResult() {
        _orderResult.value = null
    }
}
