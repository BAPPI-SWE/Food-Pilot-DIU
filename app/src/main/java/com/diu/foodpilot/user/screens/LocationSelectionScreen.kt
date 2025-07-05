
@file:OptIn(ExperimentalMaterial3Api::class)

package com.diu.foodpilot.user.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diu.foodpilot.user.data.model.Location
import com.diu.foodpilot.user.viewmodel.LocationViewModel

@Composable
fun LocationSelectionScreen(
    onLocationSelected: () -> Unit,
    locationViewModel: LocationViewModel = viewModel()
) {
    val locations by locationViewModel.locations.collectAsState()
    var selectedBaseLocation by remember { mutableStateOf<Location?>(null) }
    var selectedSubLocation by remember { mutableStateOf<String?>(null) }
    var subLocationOptions by remember { mutableStateOf<List<String>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Select Your Location",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "This helps us find restaurants that deliver to you.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(40.dp))

        // Base Location Dropdown
        LocationDropdown(
            label = "Campus / Area",
            options = locations.map { it.name },
            selectedOption = selectedBaseLocation?.name ?: "",
            onOptionSelected = { selectedName ->
                selectedBaseLocation = locations.find { it.name == selectedName }
                // When base location changes, update sub-location options and reset selection
                subLocationOptions = selectedBaseLocation?.subLocations ?: emptyList()
                selectedSubLocation = null
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sub Location Dropdown
        LocationDropdown(
            label = "Specific Hall / Building",
            options = subLocationOptions,
            selectedOption = selectedSubLocation ?: "",
            onOptionSelected = { selectedSubLocation = it },
            enabled = selectedBaseLocation != null
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                selectedBaseLocation?.let { base ->
                    selectedSubLocation?.let { sub ->
                        locationViewModel.saveUserLocation(
                            context = /* This will be provided by MainActivity */,
                            baseLocationId = base.id,
                            baseLocationName = base.name,
                            subLocation = sub
                        )
                        onLocationSelected()
                    }
                }
            },
            enabled = selectedBaseLocation != null && selectedSubLocation != null,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            Text("Confirm Location")
        }
    }
}

@Composable
private fun LocationDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
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
