package com.diu.foodpilot.user.viewmodel




import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diu.foodpilot.user.data.model.FoodCategory
import com.diu.foodpilot.user.data.model.FoodItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// This ViewModel handles the logic for the RestaurantMenuScreen.
// It takes a SavedStateHandle to retrieve the restaurantId passed during navigation.
class RestaurantMenuViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val db = Firebase.firestore

    // Safely get restaurantId from the navigation arguments
    private val restaurantId: String = checkNotNull(savedStateHandle["restaurantId"])

    private val _menuItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val menuItems: StateFlow<List<FoodItem>> = _menuItems

    // Separate flows for each category for easier management in the UI
    private val _breakfastMenu = MutableStateFlow<List<FoodItem>>(emptyList())
    val breakfastMenu: StateFlow<List<FoodItem>> = _breakfastMenu

    private val _lunchMenu = MutableStateFlow<List<FoodItem>>(emptyList())
    val lunchMenu: StateFlow<List<FoodItem>> = _lunchMenu

    private val _dinnerMenu = MutableStateFlow<List<FoodItem>>(emptyList())
    val dinnerMenu: StateFlow<List<FoodItem>> = _dinnerMenu

    private val _currentMenu = MutableStateFlow<List<FoodItem>>(emptyList())
    val currentMenu: StateFlow<List<FoodItem>> = _currentMenu


    init {
        fetchMenu()
    }

    private fun fetchMenu() {
        if (restaurantId.isEmpty()) {
            Log.e("MenuViewModel", "Restaurant ID is null or empty.")
            return
        }

        viewModelScope.launch {
            db.collection("restaurants").document(restaurantId).collection("menu")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("MenuViewModel", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val allItems = snapshot.toObjects(FoodItem::class.java)
                        _menuItems.value = allItems

                        // Filter items into their respective categories
                        _breakfastMenu.value = allItems.filter { it.category == FoodCategory.PREORDER_BREAKFAST.name }
                        _lunchMenu.value = allItems.filter { it.category == FoodCategory.PREORDER_LUNCH.name }
                        _dinnerMenu.value = allItems.filter { it.category == FoodCategory.PREORDER_DINNER.name }
                        _currentMenu.value = allItems.filter { it.category == FoodCategory.CURRENT.name }

                    } else {
                        Log.d("MenuViewModel", "Current data: null")
                    }
                }
        }
    }
}
