package com.sweetshop.customer.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sweetshop.customer.ui.components.LoadingState
import com.sweetshop.customer.ui.components.SweetShopTopBar
import com.sweetshop.customer.ui.theme.SweetCream
import com.sweetshop.customer.ui.theme.SweetPink
import com.sweetshop.customer.ui.theme.SweetPinkContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToOrders: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onNavigateToLogin()
        }
    }

    Scaffold(
        topBar = {
            SweetShopTopBar(title = "Profile")
        }
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingState()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SweetPinkContainer)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(SweetPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                modifier = Modifier.size(40.dp),
                                tint = SweetCream
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = state.user?.name ?: "Guest",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        if (!state.user?.email.isNullOrBlank()) {
                            Text(
                                text = state.user!!.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!state.user?.phone.isNullOrBlank()) {
                            Text(
                                text = state.user!!.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Menu Items
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.ListAlt,
                            title = "My Orders",
                            subtitle = "View order history",
                            onClick = onNavigateToOrders
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(
                            icon = Icons.Default.LocationOn,
                            title = "Addresses",
                            subtitle = "Manage delivery addresses",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(
                            icon = Icons.Default.Favorite,
                            title = "Wishlist",
                            subtitle = "Your favourite items",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            subtitle = "View all notifications",
                            onClick = { /* TODO */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Support section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            title = "Help & Support",
                            subtitle = "Get help with your orders",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(
                            icon = Icons.Default.GppGood,
                            title = "Terms & Conditions",
                            subtitle = "Read our terms",
                            onClick = { /* TODO */ }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(
                            icon = Icons.Default.Policy,
                            title = "Privacy Policy",
                            subtitle = "Read our privacy policy",
                            onClick = { /* TODO */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { viewModel.logout() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App version
                Text(
                    text = "Sweet Shop v1.0",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
