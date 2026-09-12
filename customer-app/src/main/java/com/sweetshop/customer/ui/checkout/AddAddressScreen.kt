package com.sweetshop.customer.ui.checkout

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.customer.ui.components.SweetShopTopBar
import com.sweetshop.customer.ui.theme.SuccessGreen
import com.sweetshop.customer.ui.theme.SweetPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    onNavigateBack: () -> Unit,
    onAddressSaved: () -> Unit,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.detectCurrentLocation()
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onAddressSaved()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            SweetShopTopBar(
                title = "Add New Address",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Use Current Location Card ──
            Card(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        state.isDetectingLocation -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = SweetPink,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Detecting location...",
                                color = SweetPink,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        state.locationDetected -> {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Location detected",
                                tint = SuccessGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Location detected",
                                color = SuccessGreen,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = "Use current location",
                                tint = SweetPink,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Use Current Location",
                                color = SweetPink,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // ── Address Type Selector ──
            Text(
                text = "Save as",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AddressTypeChip(
                    label = "Home",
                    icon = Icons.Filled.Home,
                    isSelected = state.addressType == "HOME",
                    onClick = { viewModel.selectAddressType("HOME") }
                )
                AddressTypeChip(
                    label = "Work",
                    icon = Icons.Filled.Work,
                    isSelected = state.addressType == "WORK",
                    onClick = { viewModel.selectAddressType("WORK") }
                )
                AddressTypeChip(
                    label = "Other",
                    icon = Icons.Filled.LocationOn,
                    isSelected = state.addressType == "OTHER",
                    onClick = { viewModel.selectAddressType("OTHER") }
                )
            }

            // ── Contact Details Section ──
            Text(
                text = "Contact Details",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.fullName,
                onValueChange = { viewModel.onFieldChange("fullName", it) },
                label = { Text("Full Name *") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onFieldChange("phone", it) },
                label = { Text("Phone *") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            // ── Address Details Section ──
            Text(
                text = "Address",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.addressLine1,
                onValueChange = { viewModel.onFieldChange("addressLine1", it) },
                label = { Text("House/Flat/Floor No. *") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.addressLine2,
                onValueChange = { viewModel.onFieldChange("addressLine2", it) },
                label = { Text("Area, Street, Sector") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.city,
                    onValueChange = { viewModel.onFieldChange("city", it) },
                    label = { Text("City *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.pincode,
                    onValueChange = { viewModel.onFieldChange("pincode", it) },
                    label = { Text("Pincode *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.state,
                onValueChange = { viewModel.onFieldChange("state", it) },
                label = { Text("State *") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.landmark,
                onValueChange = { viewModel.onFieldChange("landmark", it) },
                label = { Text("Landmark") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // ── Default Address Switch ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Set as default address",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = state.isDefault,
                    onCheckedChange = { viewModel.onFieldChange("isDefault", it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SweetPink
                    )
                )
            }

            // ── Save Button ──
            Button(
                onClick = { viewModel.saveAddress() },
                enabled = !state.isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SweetPink),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save Address",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressTypeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SweetPink,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = SweetPink,
            selectedBorderColor = SweetPink,
            enabled = true,
            selected = isSelected
        )
    )
}
