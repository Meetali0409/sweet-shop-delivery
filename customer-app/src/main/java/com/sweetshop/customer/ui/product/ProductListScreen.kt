package com.sweetshop.customer.ui.product

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.customer.ui.components.EmptyState
import com.sweetshop.customer.ui.components.ErrorState
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.components.ProductCard
import com.sweetshop.customer.ui.components.SweetShopSearchBar
import com.sweetshop.customer.ui.components.SweetShopTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (Long) -> Unit,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val gridState = rememberLazyGridState()

    // Pagination
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= state.products.size - 4
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && state.hasMore && !state.isLoadingMore && !state.isLoading) {
            viewModel.loadMore()
        }
    }

    LaunchedEffect(state.addToCartMessage) {
        state.addToCartMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAddToCartMessage()
        }
    }

    Scaffold(
        topBar = {
            SweetShopTopBar(
                title = "Products",
                onBackClick = onNavigateBack,
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.FilterList, "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.displayName,
                                            color = if (option == state.sortOption)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        viewModel.onSortOptionChange(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SweetShopSearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = {},
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when {
                state.isLoading -> LoadingState()
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = viewModel::loadProducts
                )
                state.products.isEmpty() -> EmptyState(
                    icon = Icons.Default.ShoppingBag,
                    title = "No products found",
                    subtitle = "Try adjusting your search or filters"
                )
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = gridState,
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.products) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onNavigateToProductDetail(product.id) },
                                onAddToCart = { viewModel.addToCart(product.id) }
                            )
                        }
                    }

                    if (state.isLoadingMore) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Loading more...")
                        }
                    }
                }
            }
        }
    }
}
