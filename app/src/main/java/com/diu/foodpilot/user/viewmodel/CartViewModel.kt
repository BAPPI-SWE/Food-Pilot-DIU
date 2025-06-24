
package com.diu.foodpilot.user.viewmodel

import androidx.lifecycle.ViewModel
import com.diu.foodpilot.user.data.model.CartItem
import com.diu.foodpilot.user.data.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// A helper data class to hold restaurant info along with its cart items
data class RestaurantCart(
    val restaurant: Restaurant,
    val items: List<CartItem>
)

class CartViewModel : ViewModel() {

    // The key is now the restaurantId, and the value is the RestaurantCart
    private val _restaurantCarts = MutableStateFlow<Map<String, RestaurantCart>>(emptyMap())
    val restaurantCarts: StateFlow<Map<String, RestaurantCart>> = _restaurantCarts.asStateFlow()

    fun addSelectionToCart(restaurant: Restaurant, selection: Map<String, CartItem>) {
        if (selection.isEmpty()) return

        _restaurantCarts.update { currentCarts ->
            val mutableCarts = currentCarts.toMutableMap()
            val existingCart = mutableCarts[restaurant.id]
            val itemsToAdd = selection.values.toList()

            if (existingCart != null) {
                // Merge new selection with existing items in the cart for this restaurant
                val updatedItems = existingCart.items.toMutableList()
                itemsToAdd.forEach { newItem ->
                    val existingItemIndex = updatedItems.indexOfFirst { it.foodId == newItem.foodId }
                    if (existingItemIndex != -1) {
                        // If item already exists, just update its quantity
                        val oldItem = updatedItems[existingItemIndex]
                        updatedItems[existingItemIndex] = oldItem.copy(quantity = oldItem.quantity + newItem.quantity)
                    } else {
                        // Otherwise, add the new item
                        updatedItems.add(newItem)
                    }
                }
                mutableCarts[restaurant.id] = existingCart.copy(items = updatedItems)
            } else {
                // If there's no cart for this restaurant yet, create a new one
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
                // If the cart for this restaurant becomes empty, remove it entirely
                mutableCarts.remove(restaurantId)
            } else {
                mutableCarts[restaurantId] = targetCart.copy(items = updatedItems)
            }
            mutableCarts
        }
    }
}
