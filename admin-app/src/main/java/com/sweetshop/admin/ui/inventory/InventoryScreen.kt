package com.sweetshop.admin.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.admin.data.dto.InventoryItemDto
import com.sweetshop.admin.ui.components.ErrorState
import com.sweetshop.admin.ui.components.LoadingState
import com.sweetshop.admin.ui.theme.AdminError
import com.sweetshop.admin.ui.theme.AdminPrimary
import com.sweetshop.admin.ui.theme.AdminSuccess
import com.sweetshop.admin.ui.theme.AdminWarning
import com.sweetshop.admin.ui.theme.InStockColor
import com.sweetshop.admin.ui.theme.LowStockColor
import com.sweetshop.admin.ui.theme.OutOfStockColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onOpenDrawer: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val filters = listOf(
        "ALL" to "All",
        "IN_STOCK" to "In Stock",
        "LOW_STOCK" to "Low Stock",
        "OUT_OF_STOCK" to "Out of Stock"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory", fontWeight = FontWeight.Bold) },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { (value, label) ->
                    FilterChip(
                        selected = state.selectedFilter == value,
                        onClick = { viewModel.onFilterChange(value) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AdminPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalItems = state.items.size
                val inStock = state.items.count { it.status.equals("IN_STOCK", true) }
                val lowStock = state.items.count { it.status.equals("LOW_STOCK", true) }
                val outOfStock = state.items.count { it.status.equals("OUT_OF_STOCK", true) }

                SummaryBadge("Total: $totalItems", Color.Gray)
                SummaryBadge("In Stock: $inStock", AdminSuccess)
                SummaryBadge("Low: $lowStock", AdminWarning)
                SummaryBadge("Out: $outOfStock", AdminError)
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoading && state.items.isEmpty() -> {
                    LoadingState()
                }
                state.error != null && state.items.isEmpty() -> {
                    ErrorState(
                        message = state.error!!,
                        onRetry = { viewModel.loadInventory() }
                    )
                }
                state.filteredItems.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Inventory,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No items found", color = Color.Gray)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.filteredItems) { item ->
                            InventoryItemCard(
                                item = item,
                                stockInput = state.stockUpdates[item.productId] ?: "",
                                isUpdating = state.updatingProductId == item.productId,
                                onStockInputChange = { viewModel.onStockInputChange(item.productId, it) },
                                onUpdateStock = { viewModel.updateStock(item.productId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryBadge(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun InventoryItemCard(
    item: InventoryItemDto,
    stockInput: String,
    isUpdating: Boolean,
    onStockInputChange: (String) -> Unit,
    onUpdateStock: () -> Unit
) {
    val (bgColor, statusColor) = when (item.status.uppercase()) {
        "IN_STOCK" -> InStockColor to AdminSuccess
        "LOW_STOCK" -> LowStockColor to AdminWarning
        "OUT_OF_STOCK" -> OutOfStockColor to AdminError
        else -> Color.White to Color.Gray
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.status.replace("_", " "),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Stock: ${item.currentStock}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = stockInput,
                    onValueChange = onStockInputChange,
                    placeholder = { Text("New qty", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = onUpdateStock,
                    enabled = !isUpdating && stockInput.isNotBlank(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary),
                    modifier = Modifier.height(48.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Update,
                            contentDescription = "Update",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
