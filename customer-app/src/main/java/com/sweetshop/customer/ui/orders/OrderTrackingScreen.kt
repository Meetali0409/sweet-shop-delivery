package com.sweetshop.customer.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.customer.domain.model.OrderStatus
import com.sweetshop.customer.ui.components.ErrorState
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.components.SweetShopTopBar
import com.sweetshop.customer.ui.theme.SweetPink
import com.sweetshop.customer.ui.theme.SuccessGreen
import com.sweetshop.customer.util.toCurrencyString
import com.sweetshop.customer.util.toDisplayDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: Long,
    onNavigateBack: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        topBar = {
            SweetShopTopBar(
                title = "Track Order",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.loadOrderDetail(orderId) }
                )
                state.selectedOrder != null -> {
                    val order = state.selectedOrder!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Order info card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Order #${order.orderNumber}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Total: ${order.total.toCurrencyString()}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    OrderStatusChip(status = order.status)
                                }

                                order.estimatedDelivery?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Estimated Delivery: ${it.toDisplayDate()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Tracking Timeline
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Order Status",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                if (order.status == OrderStatus.CANCELLED) {
                                    TrackingStep(
                                        icon = Icons.Default.Receipt,
                                        title = "Order Cancelled",
                                        subtitle = order.cancellationReason ?: "Order was cancelled",
                                        isCompleted = true,
                                        isActive = true,
                                        isLast = true,
                                        isCancelled = true
                                    )
                                } else {
                                    val steps = listOf(
                                        TrackingStepData(
                                            Icons.Default.Receipt,
                                            "Order Placed",
                                            "Your order has been placed successfully",
                                            OrderStatus.PLACED
                                        ),
                                        TrackingStepData(
                                            Icons.Default.CheckCircle,
                                            "Confirmed",
                                            "Your order has been confirmed",
                                            OrderStatus.CONFIRMED
                                        ),
                                        TrackingStepData(
                                            Icons.Default.Inventory,
                                            "Preparing",
                                            "Your order is being prepared",
                                            OrderStatus.PREPARING
                                        ),
                                        TrackingStepData(
                                            Icons.Default.DeliveryDining,
                                            "Out for Delivery",
                                            "Your order is on its way",
                                            OrderStatus.OUT_FOR_DELIVERY
                                        ),
                                        TrackingStepData(
                                            Icons.Default.ThumbUp,
                                            "Delivered",
                                            "Your order has been delivered",
                                            OrderStatus.DELIVERED
                                        )
                                    )

                                    val currentIndex = steps.indexOfFirst { it.status == order.status }

                                    steps.forEachIndexed { index, step ->
                                        TrackingStep(
                                            icon = step.icon,
                                            title = step.title,
                                            subtitle = step.subtitle,
                                            isCompleted = index <= currentIndex,
                                            isActive = index == currentIndex,
                                            isLast = index == steps.lastIndex
                                        )
                                    }
                                }
                            }
                        }

                        // Order Items
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Order Items",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                order.items.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${item.productName} x${item.quantity}",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = item.totalPrice.toCurrencyString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

private data class TrackingStepData(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val status: OrderStatus
)

@Composable
private fun TrackingStep(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isLast: Boolean,
    isCancelled: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCancelled -> MaterialTheme.colorScheme.error
                            isActive -> SweetPink
                            isCompleted -> SuccessGreen
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isCompleted || isActive || isCancelled)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            if (isCompleted) SuccessGreen
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(top = 4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isCancelled -> MaterialTheme.colorScheme.error
                    isActive -> MaterialTheme.colorScheme.primary
                    isCompleted -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!isLast) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
