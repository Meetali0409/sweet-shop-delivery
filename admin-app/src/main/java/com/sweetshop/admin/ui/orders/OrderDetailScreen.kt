package com.sweetshop.admin.ui.orders

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sweetshop.admin.data.dto.OrderDto
import com.sweetshop.admin.data.dto.OrderItemDto
import com.sweetshop.admin.ui.components.ErrorState
import com.sweetshop.admin.ui.components.LoadingState
import com.sweetshop.admin.ui.components.PaymentStatusChip
import com.sweetshop.admin.ui.components.StatusChip
import com.sweetshop.admin.ui.theme.AdminPrimary
import com.sweetshop.admin.ui.theme.AdminSuccess
import com.sweetshop.admin.util.toDisplayDate
import com.sweetshop.admin.util.toRupees

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Long,
    onNavigateBack: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()
    var showStatusConfirmDialog by remember { mutableStateOf(false) }
    var pendingStatus by remember { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Order #${state.order?.orderNumber ?: ""}",
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
        }
    ) { padding ->
        when {
            state.isLoading -> {
                LoadingState(modifier = Modifier.padding(padding))
            }
            state.error != null && state.order == null -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.loadOrderDetail(orderId) },
                    modifier = Modifier.padding(padding)
                )
            }
            state.order != null -> {
                val order = state.order!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Status & Date
                    item {
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
                                        "Order Status",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    StatusChip(status = order.orderStatus)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Payment",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    PaymentStatusChip(status = order.paymentStatus)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Placed on ${order.createdAt.toDisplayDate()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                if (!order.paymentMethod.isNullOrBlank()) {
                                    Text(
                                        text = "Payment: ${order.paymentMethod}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    // Update Status Button
                    item {
                        val nextStatus = OrderViewModel.getNextStatus(order.orderStatus)
                        if (nextStatus != null) {
                            Button(
                                onClick = {
                                    pendingStatus = nextStatus
                                    showStatusConfirmDialog = true
                                },
                                enabled = !state.isUpdatingStatus,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AdminSuccess)
                            ) {
                                if (state.isUpdatingStatus) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        nextStatus.second,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Customer Info
                    item {
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
                                Text(
                                    "Customer Details",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = AdminPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        order.customerName ?: "N/A",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = AdminPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        order.customerPhone ?: "N/A",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                if (order.address != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = AdminPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val address = order.address
                                        val addressText = buildString {
                                            append(address.addressLine1)
                                            address.addressLine2?.let { append(", $it") }
                                            append("\n${address.city}, ${address.state} - ${address.pincode}")
                                            address.landmark?.let { append("\nLandmark: $it") }
                                        }
                                        Text(
                                            addressText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Order Items
                    item {
                        Text(
                            "Items (${order.items.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(order.items) { item ->
                        OrderItemCard(item = item)
                    }

                    // Price Breakdown
                    item {
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
                                Text(
                                    "Price Breakdown",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                PriceRow("Subtotal", order.subtotal)
                                if (order.discount > 0) {
                                    PriceRow("Discount", -order.discount, isDiscount = true)
                                }
                                PriceRow("Delivery Charge", order.deliveryCharge)
                                if (order.tax > 0) {
                                    PriceRow("Tax", order.tax)
                                }
                                if (order.couponCode != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Coupon: ${order.couponCode}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AdminSuccess
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Total",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        order.totalAmount.toRupees(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AdminPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Notes
                    if (!order.notes.isNullOrBlank()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        "Notes",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        order.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    // Status update confirmation dialog
    if (showStatusConfirmDialog && pendingStatus != null) {
        AlertDialog(
            onDismissRequest = {
                showStatusConfirmDialog = false
                pendingStatus = null
            },
            title = { Text("Update Order Status") },
            text = {
                Text("Are you sure you want to change the order status to \"${pendingStatus!!.second}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateOrderStatus(orderId, pendingStatus!!.first)
                        showStatusConfirmDialog = false
                        pendingStatus = null
                    }
                ) {
                    Text("Confirm", color = AdminPrimary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showStatusConfirmDialog = false
                        pendingStatus = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun OrderItemCard(item: OrderItemDto) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.productImage,
                contentDescription = item.productName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.selectedWeight} x ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "${item.unitPrice.toRupees()} each",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Text(
                text = item.totalPrice.toRupees(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AdminPrimary
            )
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    amount: Double,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = if (isDiscount) "- ${(-amount).toRupees()}" else amount.toRupees(),
            style = MaterialTheme.typography.bodySmall,
            color = if (isDiscount) AdminSuccess else Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
    }
}
