// Open this file: app/src/main/java/com/diu/foodpilot/user/screens/ProfileScreen.kt
// Replace its contents with this updated version.

@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.diu.foodpilot.user.viewmodel.ProfileViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun ProfileScreen(profileViewModel: ProfileViewModel = viewModel()) {
    val user by profileViewModel.user.collectAsState()

    var name by remember(user) { mutableStateOf(user?.name ?: "") }
    var phone by remember(user) { mutableStateOf(user?.phone ?: "") }
    var selectedHall by remember(user) { mutableStateOf(user?.address?.location ?: "Hall 1") }
    var floor by remember(user) { mutableStateOf(user?.address?.floor ?: "") }
    var room by remember(user) { mutableStateOf(user?.address?.room ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (user == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(model = user?.profileImageUrl ?: "https://placehold.co/400x400/E53935/FFFFFF?text=${name.firstOrNull() ?: 'P'}"),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(120.dp).clip(CircleShape)
                    )
                    IconButton(
                        onClick = { /* TODO: Handle image picking */ },
                        modifier = Modifier.align(Alignment.BottomEnd).background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(Icons.Filled.Edit, "Edit Picture", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(user?.email ?: "No Email", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Text("Delivery Address", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                LocationDropdown(selectedOption = selectedHall, onOptionSelected = { selectedHall = it })
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = floor, onValueChange = { floor = it }, label = { Text("Floor No.") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room No.") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { profileViewModel.saveUserProfile(name, phone, selectedHall, floor, room) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Save Changes", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                // --- THE FIX: Simplified Sign Out Button ---
                OutlinedButton(
                    onClick = {
                        // Just sign out. The listener in MainActivity will handle the rest.
                        Firebase.auth.signOut()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Outlined.ExitToApp, contentDescription = "Sign Out")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(selectedOption: String, onOptionSelected: (String) -> Unit) {
    val options = listOf("Hall 1", "Hall 2", "Faculty Room")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Location") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
