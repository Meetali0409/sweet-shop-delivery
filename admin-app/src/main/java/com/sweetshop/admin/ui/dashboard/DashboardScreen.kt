package com.sweetshop.admin.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.admin.data.dto.InventoryItemDto
import com.sweetshop.admin.data.dto.OrderListDto
import com.sweetshop.admin.data.dto.SalesReportDto
import com.sweetshop.admin.ui.components.ErrorState
import com.sweetshop.admin.ui.components.KpiCard
import com.sweetshop.admin.ui.components.LoadingState
import com.sweetshop.admin.ui.components.StatusChip
import com.sweetshop.admin.ui.theme.AdminPrimary
import com.sweetshop.admin.ui.theme.AdminSecondary
import com.sweetshop.admin.ui.theme.AdminSuccess
import com.sweetshop.admin.ui.theme.AdminWarning
import com.sweetshop.admin.ui.theme.OutOfStockColor
import com.sweetshop.admin.ui.theme.StatusPlaced
import com.sweetshop.admin.util.toDisplayDate
import com.sweetshop.admin.util.toRupees

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToInventory: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
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
        when {
            state.isLoading && state.dashboard == null -> {
                LoadingState(modifier = Modifier.padding(padding))
            }
            state.error != null && state.dashboard == null -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.loadDashboard() },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.padding(padding)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // KPI Cards
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                KpiCard(
                                    title = "Total Orders",
                                    value = "${state.dashboard?.totalOrders ?: 0}",
                                    icon = Icons.Default.ShoppingCart,
                                    iconBackground = StatusPlaced,
                                    modifier = Modifier.weight(1f)
                                )
                                KpiCard(
                                    title = "Revenue",
                                    value = (state.dashboard?.totalRevenue ?: 0.0).toRupees(),
                                    icon = Icons.Default.CurrencyRupee,
                                    iconBackground = AdminSuccess,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                KpiCard(
                                    title = "Customers",
                                    value = "${state.dashboard?.totalCustomers ?: 0}",
                                    icon = Icons.Default.People,
                                    iconBackground = AdminSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                                KpiCard(
                                    title = "Pending",
                                    value = "${state.dashboard?.pendingOrders ?: 0}",
                                    icon = Icons.Default.Pending,
                                    iconBackground = AdminWarning,
                                    modifier = Modifier.weight(1f),
                                    subtitle = "Orders awaiting action"
                                )
                            }
                        }

                        // Sales Overview
                        item {
                            SalesOverviewSection(
                                salesData = state.salesOverview,
                                selectedPeriod = state.selectedPeriod,
                                onPeriodChange = { viewModel.loadSalesOverview(it) }
                            )
                        }

                        // Recent Orders
                        item {
                            SectionHeader(
                                title = "Recent Orders",
                                actionText = "View All",
                                onAction = onNavigateToOrders
                            )
                        }
                        val recentOrders = state.dashboard?.recentOrders ?: emptyList()
                        if (recentOrders.isEmpty()) {
                            item {
                                Text(
                                    "No recent orders",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            items(recentOrders.take(5)) { order ->
                                RecentOrderCard(order = order)
                            }
                        }

                        // Low Stock
                        item {
                            SectionHeader(
                                title = "Low Stock Alerts",
                                actionText = "View All",
                                onAction = onNavigateToInventory
                            )
                        }
                        val lowStock = state.dashboard?.lowStockProducts ?: emptyList()
                        if (lowStock.isEmpty()) {
                            item {
                                Text(
                                    "All products are well stocked",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AdminSuccess
                                )
                            }
                        } else {
                            items(lowStock) { item ->
                                LowStockCard(item = item)
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.bodyMedium,
                color = AdminPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesOverviewSection(
    salesData: List<SalesReportDto>,
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sales Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Period selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val periods = listOf("daily" to "Daily", "weekly" to "Weekly", "monthly" to "Monthly")
                items(periods) { (value, label) ->
                    FilterChip(
                        selected = selectedPeriod == value,
                        onClick = { onPeriodChange(value) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AdminPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simple bar chart
            if (salesData.isNotEmpty()) {
                val maxRevenue = salesData.maxOfOrNull { it.revenue } ?: 1.0
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = (size.width - (salesData.size - 1) * 8.dp.toPx()) / salesData.size
                        salesData.forEachIndexed { index, data ->
                            val barHeight = if (maxRevenue > 0) {
                                (data.revenue / maxRevenue * (size.height * 0.85f)).toFloat()
                            } else 0f
                            val x = index * (barWidth + 8.dp.toPx())
                            val y = size.height - barHeight

                            drawRoundRect(
                                color = AdminPrimary.copy(alpha = 0.7f + (0.3f * data.revenue / maxRevenue).toFloat()),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    salesData.take(7).forEach { data ->
                        Text(
                            text = data.periodLabel.take(3),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun RecentOrderCard(order: OrderListDto) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "#${order.orderNumber}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = order.customerName ?: "Customer",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = order.createdAt.toDisplayDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = order.totalAmount.toRupees(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AdminPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(status = order.orderStatus)
            }
        }
    }
}

@Composable
private fun LowStockCard(item: InventoryItemDto) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.currentStock == 0) OutOfStockColor else Color(0xFFFFF8E1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Low Stock",
                    tint = if (item.currentStock == 0) Color.Red else AdminWarning,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${item.currentStock} left",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (item.currentStock == 0) Color.Red else AdminWarning
            )
        }
    }
}
