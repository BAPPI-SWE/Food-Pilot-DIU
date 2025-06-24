
package com.diu.foodpilot.user.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.CartItem
import com.diu.foodpilot.user.data.model.FoodCategory
import com.diu.foodpilot.user.data.model.FoodItem
import com.diu.foodpilot.user.data.model.Restaurant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestaurantMenuViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val db = Firebase.firestore
    val restaurantId: String = checkNotNull(savedStateHandle["restaurantId"])

    // Holds details of the restaurant, including delivery status
    private val _restaurantDetails = MutableStateFlow<Restaurant?>(null)
    val restaurantDetails: StateFlow<Restaurant?> = _restaurantDetails.asStateFlow()

    // Holds the items the user selects on this screen BEFORE adding to the main cart
    private val _temporarySelection = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val temporarySelection: StateFlow<Map<String, CartItem>> = _temporarySelection.asStateFlow()

    // The menus are fetched as before
    private val _breakfastMenu = MutableStateFlow<List<FoodItem>>(emptyList())
    val breakfastMenu: StateFlow<List<FoodItem>> = _breakfastMenu

    private val _lunchMenu = MutableStateFlow<List<FoodItem>>(emptyList())
    val lunchMenu: StateFlow<List<FoodItem>> = _lunchMenu

    private val _dinnerMenu = MutableStateFlow<List<FoodItem>>(emptyList())
    val dinnerMenu: StateFlow<List<FoodItem>> = _dinnerMenu

    private val _currentMenu = MutableStateFlow<List<FoodItem>>(emptyList())
    val currentMenu: StateFlow<List<FoodItem>> = _currentMenu

    init {
        fetchRestaurantDetails()
        fetchMenu()
    }

    private fun fetchRestaurantDetails() {
        viewModelScope.launch {
            db.collection("restaurants").document(restaurantId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) { return@addSnapshotListener }
                    if (snapshot != null && snapshot.exists()) {
                        _restaurantDetails.value = snapshot.toObject(Restaurant::class.java)
                    }
                }
        }
    }

    private fun fetchMenu() {
        // This logic is mostly the same, but we don't need to check for empty restaurantId anymore
        db.collection("restaurants").document(restaurantId).collection("menu")
            .addSnapshotListener { snapshot, e ->
                if (e != null) { return@addSnapshotListener }
                if (snapshot != null) {
                    val allItems = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(FoodItem::class.java)?.copy(id = doc.id)
                    }
                    _breakfastMenu.value = allItems.filter { it.category == FoodCategory.PREORDER_BREAKFAST.name }
                    _lunchMenu.value = allItems.filter { it.category == FoodCategory.PREORDER_LUNCH.name }
                    _dinnerMenu.value = allItems.filter { it.category == FoodCategory.PREORDER_DINNER.name }
                    _currentMenu.value = allItems.filter { it.category == FoodCategory.CURRENT.name }
                }
            }
    }

    fun updateTemporarySelection(foodItem: FoodItem, change: Int) {
        _temporarySelection.update { currentSelection ->
            val mutableSelection = currentSelection.toMutableMap()
            val existingItem = mutableSelection[foodItem.id]

            if (existingItem != null) {
                val newQuantity = existingItem.quantity + change
                if (newQuantity > 0) {
                    mutableSelection[foodItem.id] = existingItem.copy(quantity = newQuantity)
                } else {
                    mutableSelection.remove(foodItem.id)
                }
            } else if (change > 0) {
                mutableSelection[foodItem.id] = CartItem(
                    foodId = foodItem.id,
                    name = foodItem.name,
                    quantity = 1,
                    price = foodItem.price
                )
            }
            mutableSelection
        }
    }

    fun clearTemporarySelection() {
        _temporarySelection.value = emptyMap()
    }
}
