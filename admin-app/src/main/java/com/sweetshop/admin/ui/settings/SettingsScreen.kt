package com.sweetshop.admin.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.sweetshop.admin.ui.components.ErrorState
import com.sweetshop.admin.ui.components.LoadingState
import com.sweetshop.admin.ui.theme.AdminPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onOpenDrawer: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.error) {
        if (!state.isLoading && state.error != null) {
            snackbarHostState.showSnackbar(state.error!!)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AdminPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when {
            state.isLoading -> {
                LoadingState()
            }
            state.error != null && state.shopName.isEmpty() -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.loadConfig() }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    // Shop Info Section
                    item {
                        SettingsSection(title = "Shop Info") {
                            SettingsTextField(
                                label = "Shop Name",
                                value = state.shopName,
                                onValueChange = { viewModel.onFieldChange("shopName", it) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Tagline",
                                value = state.tagline,
                                onValueChange = { viewModel.onFieldChange("tagline", it) }
                            )
                        }
                    }

                    // Contact Section
                    item {
                        SettingsSection(title = "Contact") {
                            SettingsTextField(
                                label = "Support Email",
                                value = state.supportEmail,
                                onValueChange = { viewModel.onFieldChange("supportEmail", it) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Support Phone",
                                value = state.supportPhone,
                                onValueChange = { viewModel.onFieldChange("supportPhone", it) }
                            )
                        }
                    }

                    // Tax & Fees Section
                    item {
                        SettingsSection(title = "Tax & Fees") {
                            SettingsTextField(
                                label = "Tax Rate (%)",
                                value = state.taxRate,
                                onValueChange = { viewModel.onFieldChange("taxRate", it) },
                                keyboardType = KeyboardType.Decimal
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Delivery Charge",
                                value = state.deliveryCharge,
                                onValueChange = { viewModel.onFieldChange("deliveryCharge", it) },
                                keyboardType = KeyboardType.Decimal
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Free Delivery Threshold",
                                value = state.freeDeliveryThreshold,
                                onValueChange = { viewModel.onFieldChange("freeDeliveryThreshold", it) },
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                    }

                    // Shop Location Section
                    item {
                        SettingsSection(title = "Shop Location") {
                            Text(
                                text = "Set your shop's coordinates for distance-based delivery",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Shop Latitude",
                                value = state.shopLatitude,
                                onValueChange = { viewModel.onFieldChange("shopLatitude", it) },
                                keyboardType = KeyboardType.Decimal
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Shop Longitude",
                                value = state.shopLongitude,
                                onValueChange = { viewModel.onFieldChange("shopLongitude", it) },
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                    }

                    // Delivery Range Section
                    item {
                        SettingsSection(title = "Delivery Range") {
                            SettingsTextField(
                                label = "Delivery Radius (km)",
                                value = state.deliveryRadiusKm,
                                onValueChange = { viewModel.onFieldChange("deliveryRadiusKm", it) },
                                keyboardType = KeyboardType.Decimal
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Per Km Charge",
                                value = state.perKmCharge,
                                onValueChange = { viewModel.onFieldChange("perKmCharge", it) },
                                keyboardType = KeyboardType.Decimal
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Base Delivery Distance (km)",
                                value = state.baseDeliveryDistanceKm,
                                onValueChange = { viewModel.onFieldChange("baseDeliveryDistanceKm", it) },
                                keyboardType = KeyboardType.Decimal
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            SettingsTextField(
                                label = "Estimated Delivery Days",
                                value = state.estimatedDeliveryDays,
                                onValueChange = { viewModel.onFieldChange("estimatedDeliveryDays", it) },
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }

                    // Save Button
                    item {
                        Button(
                            onClick = { viewModel.saveSettings() },
                            enabled = !state.isSaving,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Save Settings",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun SettingsTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
