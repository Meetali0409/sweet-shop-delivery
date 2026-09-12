package com.sweetshop.customer.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Categories : Screen("categories")
    data object ProductList : Screen("product_list/{categoryId}") {
        fun createRoute(categoryId: Long) = "product_list/$categoryId"
    }
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
    data object Cart : Screen("cart")
    data object AddressSelection : Screen("address_selection")
    data object Checkout : Screen("checkout")
    data object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: Long) = "order_confirmation/$orderId"
    }
    data object Orders : Screen("orders")
    data object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: Long) = "order_detail/$orderId"
    }
    data object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: Long) = "order_tracking/$orderId"
    }
    data object Profile : Screen("profile")
    data object Wishlist : Screen("wishlist")
}

// Bottom navigation items
enum class BottomNavItem(
    val route: String,
    val label: String
) {
    HOME("home", "Home"),
    CATEGORIES("categories", "Categories"),
    CART("cart", "Cart"),
    ORDERS("orders", "Orders"),
    PROFILE("profile", "Profile")
}
