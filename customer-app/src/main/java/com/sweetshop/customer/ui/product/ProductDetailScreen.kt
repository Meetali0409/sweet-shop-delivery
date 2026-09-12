package com.sweetshop.customer.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sweetshop.customer.domain.model.ProductVariant
import com.sweetshop.customer.ui.components.ErrorState
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.components.ProductCard
import com.sweetshop.customer.ui.components.SweetShopTopBar
import com.sweetshop.customer.ui.theme.RatingStarColor
import com.sweetshop.customer.ui.theme.SweetGold
import com.sweetshop.customer.ui.theme.SweetPink
import com.sweetshop.customer.ui.theme.SweetPinkContainer
import com.sweetshop.customer.ui.theme.SuccessGreen
import com.sweetshop.customer.ui.theme.VegGreen
import com.sweetshop.customer.util.toCurrencyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToProductDetail: (Long) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(state.addedToCart) {
        if (state.addedToCart && state.navigateToCart) {
            viewModel.clearNavigateToCart()
            onNavigateToCart()
        }
    }

    Scaffold(
        topBar = {
            SweetShopTopBar(
                title = "Product Details",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = viewModel::toggleWishlist) {
                        Icon(
                            imageVector = if (state.isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (state.isInWishlist) SweetPink else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (state.product != null && state.product!!.isAvailable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = viewModel::addToCart,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, "Cart")
                        Spacer(Modifier.width(8.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            viewModel.buyNow()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Buy Now", fontWeight = FontWeight.Bold)
                    }
                }
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
                    onRetry = viewModel::loadProduct
                )
                state.product != null -> {
                    val product = state.product!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Product Image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Discount badge
                            if (product.discountPercentage > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(12.dp)
                                        .background(SweetPink, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${product.discountPercentage.toInt()}% OFF",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            // Veg indicator
                            if (product.isVegetarian) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(12.dp)
                                        .size(24.dp)
                                        .border(2.dp, VegGreen, RoundedCornerShape(4.dp))
                                        .padding(4.dp)
                                        .background(VegGreen, CircleShape)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            // Name
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rating
                            if (product.rating > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (index < product.rating.toInt()) RatingStarColor
                                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${product.rating} (${product.reviewCount} reviews)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Price
                            Row(verticalAlignment = Alignment.Bottom) {
                                val displayPrice = state.selectedVariant?.let {
                                    it.discountedPrice ?: it.price
                                } ?: product.discountedPrice ?: product.basePrice

                                val originalPrice = state.selectedVariant?.price ?: product.basePrice

                                Text(
                                    text = displayPrice.toCurrencyString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )

                                if (displayPrice < originalPrice) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = originalPrice.toCurrencyString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        textDecoration = TextDecoration.LineThrough,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Variants (Weight selection)
                            if (product.variants.isNotEmpty()) {
                                Text(
                                    text = "Select Weight",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    product.variants.forEach { variant ->
                                        VariantChip(
                                            variant = variant,
                                            isSelected = state.selectedVariant?.id == variant.id,
                                            onClick = { viewModel.selectVariant(variant) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Quantity selector
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Quantity",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    IconButton(
                                        onClick = viewModel::decrementQuantity,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, "Decrease")
                                    }
                                    Text(
                                        text = "${state.quantity}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(40.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    IconButton(
                                        onClick = viewModel::incrementQuantity,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(Icons.Default.Add, "Increase")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))

                            // Description
                            if (product.description.isNotBlank()) {
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Ingredients
                            if (product.ingredients.isNotBlank()) {
                                InfoSection("Ingredients", product.ingredients)
                            }

                            // Allergen Info
                            if (product.allergenInfo.isNotBlank()) {
                                InfoSection("Allergen Info", product.allergenInfo)
                            }

                            // Shelf Life
                            if (product.shelfLife.isNotBlank()) {
                                InfoSection("Shelf Life", product.shelfLife)
                            }

                            // Related Products
                            if (state.relatedProducts.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "You May Also Like",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(horizontal = 0.dp)
                                ) {
                                    items(state.relatedProducts) { product ->
                                        ProductCard(
                                            product = product,
                                            onClick = { onNavigateToProductDetail(product.id) },
                                            onAddToCart = {},
                                            modifier = Modifier.width(170.dp)
                                        )
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
}

@Composable
private fun VariantChip(
    variant: ProductVariant,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(enabled = variant.isAvailable) { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SweetPinkContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, SweetPink)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = variant.weight,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) SweetPink else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = (variant.discountedPrice ?: variant.price).toCurrencyString(),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) SweetPink else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoSection(title: String, content: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = content,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(12.dp))
}
