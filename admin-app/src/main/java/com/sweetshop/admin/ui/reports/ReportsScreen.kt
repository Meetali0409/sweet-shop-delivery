package com.sweetshop.admin.ui.reports

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.admin.ui.components.ErrorState
import com.sweetshop.admin.ui.components.KpiCard
import com.sweetshop.admin.ui.components.LoadingState
import com.sweetshop.admin.ui.theme.AdminPrimary
import com.sweetshop.admin.ui.theme.AdminSecondary
import com.sweetshop.admin.ui.theme.AdminSuccess
import com.sweetshop.admin.ui.theme.StatusPlaced
import com.sweetshop.admin.util.toRupees

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onOpenDrawer: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val periods = listOf(
        "daily" to "Today",
        "weekly" to "This Week",
        "monthly" to "This Month"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports", fontWeight = FontWeight.Bold) },
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
            state.isLoading && state.salesOverview.isEmpty() && state.salesReport == null -> {
                LoadingState(modifier = Modifier.padding(padding))
            }
            state.error != null && state.salesOverview.isEmpty() -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.loadSalesOverview(state.selectedPeriod) },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Period Selector
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(periods) { (value, label) ->
                                FilterChip(
                                    selected = state.selectedPeriod == value,
                                    onClick = { viewModel.onPeriodChange(value) },
                                    label = { Text(label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AdminPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Custom Date Range
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Custom Range",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = state.dateFrom,
                                        onValueChange = { viewModel.onDateFromChange(it) },
                                        label = { Text("From", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodySmall
                                    )
                                    OutlinedTextField(
                                        value = state.dateTo,
                                        onValueChange = { viewModel.onDateToChange(it) },
                                        label = { Text("To", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodySmall
                                    )
                                    Button(
                                        onClick = { viewModel.applyCustomDates() },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AdminPrimary),
                                        modifier = Modifier.height(48.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "Apply",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sales Summary KPIs
                    if (state.salesReport != null) {
                        item {
                            Text(
                                "Sales Summary",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                KpiCard(
                                    title = "Revenue",
                                    value = state.salesReport!!.revenue.toRupees(),
                                    icon = Icons.Default.CurrencyRupee,
                                    iconBackground = AdminSuccess,
                                    modifier = Modifier.weight(1f)
                                )
                                KpiCard(
                                    title = "Orders",
                                    value = "${state.salesReport!!.orders}",
                                    icon = Icons.Default.ShoppingCart,
                                    iconBackground = StatusPlaced,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        item {
                            KpiCard(
                                title = "Avg Order Value",
                                value = state.salesReport!!.averageOrderValue.toRupees(),
                                icon = Icons.Default.TrendingUp,
                                iconBackground = AdminSecondary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Sales Chart
                    if (state.salesOverview.isNotEmpty()) {
                        item {
                            Text(
                                "Sales Trend",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    val maxRevenue = state.salesOverview.maxOfOrNull { it.revenue } ?: 1.0

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val barCount = state.salesOverview.size
                                            if (barCount == 0) return@Canvas
                                            val gap = 6.dp.toPx()
                                            val barWidth = (size.width - (barCount - 1) * gap) / barCount

                                            state.salesOverview.forEachIndexed { index, data ->
                                                val barHeight = if (maxRevenue > 0) {
                                                    (data.revenue / maxRevenue * (size.height * 0.9f)).toFloat()
                                                } else 0f
                                                val x = index * (barWidth + gap)
                                                val y = size.height - barHeight

                                                drawRoundRect(
                                                    color = AdminPrimary.copy(
                                                        alpha = 0.5f + (0.5f * data.revenue / maxRevenue).toFloat()
                                                    ),
                                                    topLeft = Offset(x, y),
                                                    size = Size(barWidth, barHeight),
                                                    cornerRadius = CornerRadius(4.dp.toPx())
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Labels
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        state.salesOverview.forEach { data ->
                                            Text(
                                                text = data.periodLabel.take(3),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Detail Table
                        item {
                            Text(
                                "Period Breakdown",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(state.salesOverview) { data ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                            text = data.periodLabel,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${data.orders} orders",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = data.revenue.toRupees(),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = AdminPrimary
                                        )
                                        Text(
                                            text = "Avg: ${data.averageOrderValue.toRupees()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
