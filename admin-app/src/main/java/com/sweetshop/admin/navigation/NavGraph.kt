package com.sweetshop.admin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sweetshop.admin.ui.auth.AdminLoginScreen
import com.sweetshop.admin.ui.categories.CategoryListScreen
import com.sweetshop.admin.ui.coupons.AddEditCouponScreen
import com.sweetshop.admin.ui.coupons.CouponListScreen
import com.sweetshop.admin.ui.customers.CustomerListScreen
import com.sweetshop.admin.ui.dashboard.DashboardScreen
import com.sweetshop.admin.ui.inventory.InventoryScreen
import com.sweetshop.admin.ui.orders.OrderDetailScreen
import com.sweetshop.admin.ui.orders.OrderListScreen
import com.sweetshop.admin.ui.products.AddEditProductScreen
import com.sweetshop.admin.ui.products.ProductListScreen
import com.sweetshop.admin.ui.reports.ReportsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onOpenDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            AdminLoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onOpenDrawer = onOpenDrawer,
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToInventory = {
                    navController.navigate(Screen.Inventory.route)
                }
            )
        }

        composable(Screen.Products.route) {
            ProductListScreen(
                onOpenDrawer = onOpenDrawer,
                onAddProduct = {
                    navController.navigate(Screen.AddProduct.route)
                },
                onEditProduct = { productId ->
                    navController.navigate(Screen.EditProduct.createRoute(productId))
                }
            )
        }

        composable(Screen.AddProduct.route) {
            AddEditProductScreen(
                productId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId")
            AddEditProductScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Categories.route) {
            CategoryListScreen(onOpenDrawer = onOpenDrawer)
        }

        composable(Screen.Orders.route) {
            OrderListScreen(
                onOpenDrawer = onOpenDrawer,
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                }
            )
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
            OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Customers.route) {
            CustomerListScreen(onOpenDrawer = onOpenDrawer)
        }

        composable(Screen.Coupons.route) {
            CouponListScreen(
                onOpenDrawer = onOpenDrawer,
                onAddCoupon = {
                    navController.navigate(Screen.AddCoupon.route)
                },
                onEditCoupon = { couponId ->
                    navController.navigate(Screen.EditCoupon.createRoute(couponId))
                }
            )
        }

        composable(Screen.AddCoupon.route) {
            AddEditCouponScreen(
                couponId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditCoupon.route,
            arguments = listOf(navArgument("couponId") { type = NavType.LongType })
        ) { backStackEntry ->
            val couponId = backStackEntry.arguments?.getLong("couponId")
            AddEditCouponScreen(
                couponId = couponId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Inventory.route) {
            InventoryScreen(onOpenDrawer = onOpenDrawer)
        }

        composable(Screen.Reports.route) {
            ReportsScreen(onOpenDrawer = onOpenDrawer)
        }
    }
}
