package com.sweetshop.customer.ui.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.customer.domain.model.Category
import com.sweetshop.customer.domain.model.Product
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.components.ProductCard
import com.sweetshop.customer.ui.components.SweetShopSearchBar
import com.sweetshop.customer.ui.theme.SweetCream
import com.sweetshop.customer.ui.theme.SweetGold
import com.sweetshop.customer.ui.theme.SweetPink
import com.sweetshop.customer.ui.theme.SweetPinkContainer

@Composable
fun HomeScreen(
    onNavigateToProductDetail: (Long) -> Unit,
    onNavigateToCategory: (Long) -> Unit,
    onNavigateToCategories: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.addToCartMessage) {
        state.addToCartMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAddToCartMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            LoadingState(message = "Loading delicious treats...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                HomeHeader()

                // Search bar
                SweetShopSearchBar(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onSearch = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Promotional Banner
                PromotionalBanner()

                Spacer(modifier = Modifier.height(20.dp))

                // Categories section
                if (state.categories.isNotEmpty()) {
                    SectionHeader(
                        title = "Categories",
                        onSeeAllClick = onNavigateToCategories
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoriesRow(
                        categories = state.categories,
                        onCategoryClick = onNavigateToCategory
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Featured Products
                if (state.featuredProducts.isNotEmpty()) {
                    SectionHeader(title = "Popular Sweets")
                    Spacer(modifier = Modifier.height(8.dp))
                    ProductsRow(
                        products = state.featuredProducts,
                        onProductClick = onNavigateToProductDetail,
                        onAddToCart = viewModel::addToCart
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Bestsellers
                if (state.bestsellers.isNotEmpty()) {
                    SectionHeader(title = "Bestsellers")
                    Spacer(modifier = Modifier.height(8.dp))
                    ProductsRow(
                        products = state.bestsellers,
                        onProductClick = onNavigateToProductDetail,
                        onAddToCart = viewModel::addToCart
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // New Arrivals
                if (state.newArrivals.isNotEmpty()) {
                    SectionHeader(title = "New Arrivals")
                    Spacer(modifier = Modifier.height(8.dp))
                    ProductsRow(
                        products = state.newArrivals,
                        onProductClick = onNavigateToProductDetail,
                        onAddToCart = viewModel::addToCart
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Sweet Shop",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SweetCream
            )
            Text(
                text = "Fresh & Delicious Sweets",
                style = MaterialTheme.typography.bodySmall,
                color = SweetCream.copy(alpha = 0.8f)
            )
        }
        IconButton(onClick = { /* TODO: Notifications */ }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = SweetCream
            )
        }
    }
}

@Composable
private fun PromotionalBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SweetPinkContainer
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(
                            SweetPink.copy(alpha = 0.9f),
                            SweetGold.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Fresh & Delicious",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SweetCream
                )
                Text(
                    text = "Sweets Made with Love",
                    fontSize = 16.sp,
                    color = SweetCream.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .background(SweetCream, RoundedCornerShape(20.dp))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Shop Now",
                        fontWeight = FontWeight.Bold,
                        color = SweetPink,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (onSeeAllClick != null) {
            TextButton(onClick = onSeeAllClick) {
                Text(
                    text = "See All",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CategoriesRow(
    categories: List<Category>,
    onCategoryClick: (Long) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryChip(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .width(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SweetPinkContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = category.name,
                    tint = SweetPink,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProductsRow(
    products: List<Product>,
    onProductClick: (Long) -> Unit,
    onAddToCart: (Long) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
                onAddToCart = { onAddToCart(product.id) },
                modifier = Modifier.width(170.dp)
            )
        }
    }
}
