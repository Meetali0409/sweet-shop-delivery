package com.sweetshop.customer.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sweetshop.customer.ui.auth.LoginScreen
import com.sweetshop.customer.ui.auth.RegisterScreen
import com.sweetshop.customer.ui.cart.CartScreen
import com.sweetshop.customer.ui.categories.CategoriesScreen
import com.sweetshop.customer.ui.checkout.AddAddressScreen
import com.sweetshop.customer.ui.checkout.AddressSelectionScreen
import com.sweetshop.customer.ui.checkout.CheckoutScreen
import com.sweetshop.customer.ui.checkout.OrderConfirmationScreen
import com.sweetshop.customer.ui.components.SweetShopBottomNavBar
import com.sweetshop.customer.ui.home.HomeScreen
import com.sweetshop.customer.ui.orders.OrderDetailScreen
import com.sweetshop.customer.ui.orders.OrderHistoryScreen
import com.sweetshop.customer.ui.orders.OrderTrackingScreen
import com.sweetshop.customer.ui.product.ProductDetailScreen
import com.sweetshop.customer.ui.product.ProductListScreen
import com.sweetshop.customer.ui.profile.ProfileScreen
import com.sweetshop.customer.ui.splash.SplashScreen
import com.sweetshop.customer.ui.wishlist.WishlistScreen

@Composable
fun SweetShopNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var cartItemCount by remember { mutableIntStateOf(0) }

    // Bottom nav routes
    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.Categories.route,
        Screen.Cart.route,
        Screen.Orders.route,
        Screen.Profile.route
    )
    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                SweetShopBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    cartItemCount = cartItemCount
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = if (showBottomNav) Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                else Modifier
        ) {
            // Splash
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    },
                    onContinueAsGuest = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Home
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToCategory = { categoryId ->
                        navController.navigate(Screen.ProductList.createRoute(categoryId))
                    },
                    onNavigateToCategories = {
                        navController.navigate(Screen.Categories.route)
                    }
                )
            }

            // Categories
            composable(Screen.Categories.route) {
                CategoriesScreen(
                    onNavigateToProductList = { categoryId ->
                        navController.navigate(Screen.ProductList.createRoute(categoryId))
                    }
                )
            }

            // Product List
            composable(
                route = Screen.ProductList.route,
                arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
            ) {
                ProductListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }

            // Product Detail
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) {
                ProductDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCart = {
                        navController.navigate(Screen.Cart.route)
                    },
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }

            // Cart
            composable(Screen.Cart.route) {
                CartScreen(
                    onNavigateToCheckout = {
                        navController.navigate(Screen.AddressSelection.route)
                    },
                    onNavigateToShopping = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // Address Selection
            composable(Screen.AddressSelection.route) {
                AddressSelectionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAddressSelected = {
                        navController.navigate(Screen.Checkout.route)
                    },
                    onAddAddress = {
                        navController.navigate(Screen.AddAddress.route)
                    }
                )
            }

            // Add Address
            composable(Screen.AddAddress.route) {
                AddAddressScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onAddressSaved = { navController.popBackStack() }
                )
            }

            // Checkout
            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddressSelection = {
                        navController.navigate(Screen.AddressSelection.route)
                    },
                    onOrderPlaced = { orderId ->
                        navController.navigate(Screen.OrderConfirmation.createRoute(orderId)) {
                            popUpTo(Screen.Cart.route) { inclusive = true }
                        }
                    }
                )
            }

            // Order Confirmation
            composable(
                route = Screen.OrderConfirmation.route,
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
                OrderConfirmationScreen(
                    orderId = orderId,
                    onNavigateToOrderTracking = { id ->
                        navController.navigate(Screen.OrderTracking.createRoute(id))
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // Orders (from bottom nav - no back button)
            composable(Screen.Orders.route) {
                OrderHistoryScreen(
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    }
                )
            }

            // Orders (from profile - with back button)
            composable("orders_from_profile") {
                OrderHistoryScreen(
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Order Detail
            composable(
                route = Screen.OrderDetail.route,
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
                OrderDetailScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTracking = { id ->
                        navController.navigate(Screen.OrderTracking.createRoute(id))
                    }
                )
            }

            // Order Tracking
            composable(
                route = Screen.OrderTracking.route,
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
                OrderTrackingScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Profile
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToOrders = {
                        navController.navigate("orders_from_profile")
                    },
                    onNavigateToAddresses = {
                        navController.navigate(Screen.AddressSelection.route)
                    },
                    onNavigateToWishlist = {
                        navController.navigate(Screen.Wishlist.route)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Wishlist
            composable(Screen.Wishlist.route) {
                WishlistScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }
        }
    }
}
