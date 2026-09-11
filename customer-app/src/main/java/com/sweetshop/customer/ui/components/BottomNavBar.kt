package com.sweetshop.customer.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.sweetshop.customer.navigation.BottomNavItem
import com.sweetshop.customer.ui.theme.SweetPink

data class BottomNavItemData(
    val item: BottomNavItem,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun SweetShopBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    cartItemCount: Int = 0
) {
    val items = listOf(
        BottomNavItemData(BottomNavItem.HOME, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItemData(BottomNavItem.CATEGORIES, Icons.Filled.Category, Icons.Outlined.Category),
        BottomNavItemData(BottomNavItem.CART, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
        BottomNavItemData(BottomNavItem.ORDERS, Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
        BottomNavItemData(BottomNavItem.PROFILE, Icons.Filled.Person, Icons.Outlined.Person)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { navItem ->
            val isSelected = currentRoute == navItem.item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(navItem.item.route) },
                icon = {
                    if (navItem.item == BottomNavItem.CART && cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = SweetPink) {
                                    Text(
                                        text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                                contentDescription = navItem.item.label
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                            contentDescription = navItem.item.label
                        )
                    }
                },
                label = {
                    Text(
                        text = navItem.item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
