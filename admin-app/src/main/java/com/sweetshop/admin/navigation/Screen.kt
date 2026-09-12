package com.sweetshop.admin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Login : Screen("login", "Login", Icons.Default.Lock)
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Products : Screen("products", "Products", Icons.Default.ShoppingBag)
    data object AddProduct : Screen("add_product", "Add Product", Icons.Default.Add)
    data object EditProduct : Screen("edit_product/{productId}", "Edit Product", Icons.Default.Edit) {
        fun createRoute(id: Long) = "edit_product/$id"
    }
    data object Categories : Screen("categories", "Categories", Icons.Default.Category)
    data object Orders : Screen("orders", "Orders", Icons.Default.Receipt)
    data object OrderDetail : Screen("order_detail/{orderId}", "Order Detail", Icons.Default.Info) {
        fun createRoute(id: Long) = "order_detail/$id"
    }
    data object Customers : Screen("customers", "Customers", Icons.Default.People)
    data object Coupons : Screen("coupons", "Coupons", Icons.Default.LocalOffer)
    data object AddCoupon : Screen("add_coupon", "Add Coupon", Icons.Default.Add)
    data object EditCoupon : Screen("edit_coupon/{couponId}", "Edit Coupon", Icons.Default.Edit) {
        fun createRoute(id: Long) = "edit_coupon/$id"
    }
    data object Inventory : Screen("inventory", "Inventory", Icons.Default.Inventory)
    data object Reports : Screen("reports", "Reports", Icons.Default.BarChart)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}
