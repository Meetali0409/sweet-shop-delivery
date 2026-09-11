package com.sweetshop.customer.ui.orders

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sweetshop.customer.domain.model.Order
import com.sweetshop.customer.domain.model.OrderStatus
import com.sweetshop.customer.ui.components.EmptyState
import com.sweetshop.customer.ui.components.ErrorState
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.components.SweetShopTopBar
import com.sweetshop.customer.ui.theme.ErrorRed
import com.sweetshop.customer.ui.theme.SweetGold
import com.sweetshop.customer.ui.theme.SuccessGreen
import com.sweetshop.customer.util.toCurrencyString
import com.sweetshop.customer.util.toDisplayDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onNavigateToOrderDetail: (Long) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val tabs = listOf("All", "Processing", "Delivered", "Cancelled")

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            SweetShopTopBar(title = "My Orders", onBackClick = onNavigateBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter tabs
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs) { tab ->
                    FilterChip(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        label = { Text(tab) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = viewModel::loadOrders
                )
                state.orders.isEmpty() -> EmptyState(
                    icon = Icons.Default.ListAlt,
                    title = "No orders yet",
                    subtitle = "Your order history will appear here"
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.orders) { order ->
                            OrderCard(
                                order = order,
                                onClick = { onNavigateToOrderDetail(order.id) },
                                onReorder = { viewModel.reorder(order.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    onReorder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Order header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.orderNumber}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    order.createdAt?.let {
                        Text(
                            text = it.toDisplayDate(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OrderStatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order items thumbnails
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                order.items.take(3).forEach { item ->
                    AsyncImage(
                        model = item.productImageUrl,
                        contentDescription = item.productName,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                if (order.items.size > 3) {
                    Text(
                        text = "+${order.items.size - 3} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items summary
            Text(
                text = order.items.joinToString(", ") { "${it.productName} x${it.quantity}" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.total.toCurrencyString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (order.status == OrderStatus.DELIVERED || order.status == OrderStatus.CANCELLED) {
                        OutlinedButton(
                            onClick = onReorder,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                "Reorder",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reorder", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusChip(status: OrderStatus) {
    val (color, bgColor) = when (status) {
        OrderStatus.PLACED, OrderStatus.CONFIRMED -> Pair(
            SweetGold, SweetGold.copy(alpha = 0.1f)
        )
        OrderStatus.PREPARING, OrderStatus.OUT_FOR_DELIVERY -> Pair(
            MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer
        )
        OrderStatus.DELIVERED -> Pair(SuccessGreen, SuccessGreen.copy(alpha = 0.1f))
        OrderStatus.CANCELLED -> Pair(ErrorRed, ErrorRed.copy(alpha = 0.1f))
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = status.displayName(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = color
            )
        },
        colors = AssistChipDefaults.assistChipColors(containerColor = bgColor),
        border = null
    )
}
