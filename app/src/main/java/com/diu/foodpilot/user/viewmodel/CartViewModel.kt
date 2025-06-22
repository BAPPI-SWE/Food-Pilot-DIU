
package com.diu.foodpilot.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.CartItem
import com.diu.foodpilot.user.data.model.FoodItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Expose the total number of items for the badge
    // The missing imports for this line are now added above.
    val totalItemCount: StateFlow<Int> = cartItems.map { list ->
        list.sumOf { it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0
    )


    fun addItem(foodItem: FoodItem) {
        _cartItems.update { currentList ->
            val existingItem = currentList.find { it.foodId == foodItem.id }
            if (existingItem != null) {
                // If item exists, update its quantity
                currentList.map {
                    if (it.foodId == foodItem.id) {
                        it.copy(quantity = it.quantity + 1)
                    } else {
                        it
                    }
                }
            } else {
                // If item doesn't exist, add it to the list
                currentList + CartItem(
                    foodId = foodItem.id,
                    name = foodItem.name,
                    quantity = 1,
                    price = foodItem.price
                )
            }
        }
    }

    fun removeItem(foodItem: FoodItem) {
        _cartItems.update { currentList ->
            val existingItem = currentList.find { it.foodId == foodItem.id }
            if (existingItem != null && existingItem.quantity > 1) {
                // If quantity > 1, decrease it
                currentList.map {
                    if (it.foodId == foodItem.id) {
                        it.copy(quantity = it.quantity - 1)
                    } else {
                        it
                    }
                }
            } else {
                // If quantity is 1 or item doesn't exist, remove it entirely
                currentList.filterNot { it.foodId == foodItem.id }
            }
        }
    }
}
