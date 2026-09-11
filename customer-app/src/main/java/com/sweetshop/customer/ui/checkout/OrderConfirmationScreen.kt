package com.sweetshop.customer.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.orders.OrdersViewModel
import com.sweetshop.customer.ui.theme.SuccessGreen
import com.sweetshop.customer.util.toCurrencyString
import com.sweetshop.customer.util.toDisplayDate

@Composable
fun OrderConfirmationScreen(
    orderId: Long,
    onNavigateToOrderTracking: (Long) -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val order = state.selectedOrder

    if (state.isLoading) {
        LoadingState()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(SuccessGreen.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Order Placed",
                    modifier = Modifier.size(64.dp),
                    tint = SuccessGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Order Placed Successfully!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Thank you for your order",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Order details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (order != null) {
                        OrderInfoRow("Order Number", "#${order.orderNumber}")
                        OrderInfoRow("Total Amount", order.total.toCurrencyString())
                        OrderInfoRow("Payment Method", order.paymentMethod)
                        order.estimatedDelivery?.let {
                            OrderInfoRow("Est. Delivery", it.toDisplayDate())
                        }
                        order.shippingAddress?.let { addr ->
                            OrderInfoRow("Delivery to", addr.formattedAddress())
                        }
                    } else {
                        OrderInfoRow("Order ID", "#$orderId")
                        Text(
                            text = "Your order has been placed successfully.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Track Order button
            Button(
                onClick = { onNavigateToOrderTracking(orderId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Track Order",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Continue Shopping button
            OutlinedButton(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue Shopping",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun OrderInfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
