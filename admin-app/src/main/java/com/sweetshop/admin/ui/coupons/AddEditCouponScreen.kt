package com.sweetshop.admin.ui.coupons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.admin.ui.components.LoadingState
import com.sweetshop.admin.ui.theme.AdminPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCouponScreen(
    couponId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: CouponViewModel = hiltViewModel()
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val isEditing = couponId != null

    LaunchedEffect(couponId) {
        if (couponId != null) {
            viewModel.loadCoupon(couponId)
        } else {
            viewModel.resetFormState()
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Coupon" else "Add Coupon",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        if (state.isLoading) {
            LoadingState(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Coupon Code
                OutlinedTextField(
                    value = state.couponCode,
                    onValueChange = { viewModel.onFormFieldChange("couponCode", it.uppercase()) },
                    label = { Text("Coupon Code *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isEditing
                )

                // Description
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.onFormFieldChange("description", it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                // Discount Type
                var discountTypeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = discountTypeExpanded,
                    onExpandedChange = { discountTypeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = if (state.discountType == "PERCENTAGE") "Percentage" else "Fixed Amount",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Discount Type *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = discountTypeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = discountTypeExpanded,
                        onDismissRequest = { discountTypeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Percentage") },
                            onClick = {
                                viewModel.onFormFieldChange("discountType", "PERCENTAGE")
                                discountTypeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Fixed Amount") },
                            onClick = {
                                viewModel.onFormFieldChange("discountType", "FIXED")
                                discountTypeExpanded = false
                            }
                        )
                    }
                }

                // Discount Value
                OutlinedTextField(
                    value = state.discountValue,
                    onValueChange = { viewModel.onFormFieldChange("discountValue", it) },
                    label = {
                        Text(
                            if (state.discountType == "PERCENTAGE") "Discount (%)" else "Discount Amount"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    prefix = {
                        if (state.discountType == "FIXED") Text("\u20B9 ")
                    },
                    suffix = {
                        if (state.discountType == "PERCENTAGE") Text("%")
                    }
                )

                // Min Order Value & Max Discount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.minimumOrderValue,
                        onValueChange = { viewModel.onFormFieldChange("minimumOrderValue", it) },
                        label = { Text("Min Order *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        prefix = { Text("\u20B9 ") }
                    )
                    OutlinedTextField(
                        value = state.maximumDiscount,
                        onValueChange = { viewModel.onFormFieldChange("maximumDiscount", it) },
                        label = { Text("Max Discount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        prefix = { Text("\u20B9 ") }
                    )
                }

                // Valid From/To
                OutlinedTextField(
                    value = state.validFrom,
                    onValueChange = { viewModel.onFormFieldChange("validFrom", it) },
                    label = { Text("Valid From * (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = state.validUntil,
                    onValueChange = { viewModel.onFormFieldChange("validUntil", it) },
                    label = { Text("Valid Until * (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Usage Limit
                OutlinedTextField(
                    value = state.usageLimit,
                    onValueChange = { viewModel.onFormFieldChange("usageLimit", it) },
                    label = { Text("Usage Limit (leave empty for unlimited)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Active Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = state.isActive,
                        onCheckedChange = { viewModel.onFormFieldChange("isActive", it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AdminPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
                Button(
                    onClick = { viewModel.saveCoupon() },
                    enabled = !state.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isEditing) "Update Coupon" else "Create Coupon",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
