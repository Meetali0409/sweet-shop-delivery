package com.sweetshop.customer.ui.cart

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.sweetshop.customer.domain.model.CartItem
import com.sweetshop.customer.ui.components.EmptyState
import com.sweetshop.customer.ui.components.ErrorState
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.components.SweetShopTopBar
import com.sweetshop.customer.ui.theme.SuccessGreen
import com.sweetshop.customer.util.toCurrencyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateToCheckout: () -> Unit,
    onNavigateToShopping: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            SweetShopTopBar(
                title = "My Cart",
                actions = {
                    if (state.cart.items.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearCart) {
                            Icon(Icons.Default.Delete, "Clear Cart")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (state.cart.items.isNotEmpty()) {
                CartBottomBar(
                    total = state.cart.total,
                    itemCount = state.cart.items.size,
                    onCheckout = onNavigateToCheckout
                )
            }
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
                    onRetry = viewModel::loadCart
                )
                state.cart.items.isEmpty() -> EmptyState(
                    icon = Icons.Default.ShoppingCart,
                    title = "Your cart is empty",
                    subtitle = "Add some delicious sweets to your cart",
                    actionLabel = "Browse Sweets",
                    onAction = onNavigateToShopping
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(state.cart.items, key = { it.id }) { item ->
                            CartItemCard(
                                item = item,
                                onUpdateQuantity = { viewModel.updateQuantity(item.id, it) },
                                onRemove = { viewModel.removeItem(item.id) }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }

                        // Coupon section
                        item {
                            CouponSection(
                                couponCode = state.couponCode,
                                appliedCoupon = state.cart.couponCode,
                                couponMessage = state.couponMessage,
                                isApplying = state.isApplyingCoupon,
                                onCodeChange = viewModel::updateCouponCode,
                                onApply = viewModel::applyCoupon,
                                onRemove = viewModel::removeCoupon
                            )
                        }

                        // Price breakdown
                        item {
                            PriceBreakdown(
                                subtotal = state.cart.subtotal,
                                discount = state.cart.discount,
                                couponDiscount = state.cart.couponDiscount,
                                deliveryFee = state.cart.deliveryFee,
                                tax = state.cart.tax,
                                total = state.cart.total
                            )
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image
        AsyncImage(
            model = item.productImageUrl,
            contentDescription = item.productName,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Remove",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (item.weight.isNotBlank()) {
                Text(
                    text = item.weight,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            "Decrease",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            "Increase",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = item.totalPrice.toCurrencyString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CouponSection(
    couponCode: String,
    appliedCoupon: String?,
    couponMessage: String?,
    isApplying: Boolean,
    onCodeChange: (String) -> Unit,
    onApply: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Apply Coupon",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (appliedCoupon != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Coupon '$appliedCoupon' applied",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Medium
                    )
                    TextButton(onClick = onRemove) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = couponCode,
                        onValueChange = onCodeChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Enter coupon code") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedButton(
                        onClick = onApply,
                        enabled = !isApplying && couponCode.isNotBlank()
                    ) {
                        if (isApplying) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        } else {
                            Text("Apply")
                        }
                    }
                }
            }

            if (couponMessage != null && appliedCoupon == null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = couponMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun PriceBreakdown(
    subtotal: Double,
    discount: Double,
    couponDiscount: Double,
    deliveryFee: Double,
    tax: Double,
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Price Details",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            PriceRow("Subtotal", subtotal.toCurrencyString())
            if (discount > 0) {
                PriceRow("Discount", "-${discount.toCurrencyString()}", isDiscount = true)
            }
            if (couponDiscount > 0) {
                PriceRow("Coupon Discount", "-${couponDiscount.toCurrencyString()}", isDiscount = true)
            }
            PriceRow(
                "Delivery Fee",
                if (deliveryFee > 0) deliveryFee.toCurrencyString() else "FREE",
                isDiscount = deliveryFee == 0.0
            )
            if (tax > 0) {
                PriceRow("Tax", tax.toCurrencyString())
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = total.toCurrencyString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PriceRow(
    label: String,
    value: String,
    isDiscount: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isDiscount) SuccessGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CartBottomBar(
    total: Double,
    itemCount: Int,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = total.toCurrencyString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "$itemCount item(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onCheckout,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Proceed to Checkout",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
