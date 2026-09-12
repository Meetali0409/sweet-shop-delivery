package com.sweetshop.customer.ui.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.customer.ui.components.EmptyState
import com.sweetshop.customer.ui.components.ErrorState
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.components.ProductCard
import com.sweetshop.customer.ui.components.SweetShopTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProductDetail: (Long) -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SweetShopTopBar(
                title = "My Wishlist",
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
                    onRetry = viewModel::loadWishlist
                )
                state.products.isEmpty() -> EmptyState(
                    icon = Icons.Default.FavoriteBorder,
                    title = "Your wishlist is empty",
                    subtitle = "Add items you love to your wishlist"
                )
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.products) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onNavigateToProductDetail(product.id) },
                                onAddToCart = { viewModel.removeFromWishlist(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
