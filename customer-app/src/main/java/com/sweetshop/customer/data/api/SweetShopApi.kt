package com.sweetshop.customer.data.api

import com.sweetshop.customer.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface SweetShopApi {

    // ==================== Config ====================

    @GET("config")
    suspend fun getShopConfig(): Response<ApiResponse<ShopConfigDto>>

    // ==================== Auth ====================

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<AuthResponse>>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>

    // ==================== Products ====================

    @GET("products")
    suspend fun getProducts(
        @Query("category") categoryId: Long? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<PagedResponse<ProductDto>>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Long): Response<ApiResponse<ProductDto>>

    @GET("products/featured")
    suspend fun getFeaturedProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PagedResponse<ProductDto>>>

    @GET("products/bestsellers")
    suspend fun getBestsellers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PagedResponse<ProductDto>>>

    // ==================== Categories ====================

    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    // ==================== Cart ====================

    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<CartDto>>

    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<ApiResponse<CartDto>>

    @PUT("cart/items/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: Long,
        @Body request: UpdateCartItemRequest
    ): Response<ApiResponse<CartDto>>

    @DELETE("cart/items/{itemId}")
    suspend fun removeCartItem(@Path("itemId") itemId: Long): Response<ApiResponse<CartDto>>

    @DELETE("cart")
    suspend fun clearCart(): Response<ApiResponse<Unit>>

    @POST("cart/coupon")
    suspend fun applyCoupon(@Body request: ApplyCouponRequest): Response<ApiResponse<CartDto>>

    @DELETE("cart/coupon")
    suspend fun removeCoupon(): Response<ApiResponse<CartDto>>

    // ==================== Orders ====================

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderDto>>

    @GET("orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<OrderDto>>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: Long): Response<ApiResponse<OrderDto>>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(
        @Path("id") orderId: Long,
        @Body request: CancelOrderRequest
    ): Response<ApiResponse<OrderDto>>

    @POST("orders/{id}/reorder")
    suspend fun reorder(@Path("id") orderId: Long): Response<ApiResponse<OrderDto>>

    // ==================== Addresses ====================

    @GET("addresses")
    suspend fun getAddresses(): Response<ApiResponse<List<AddressDto>>>

    @POST("addresses")
    suspend fun createAddress(@Body request: CreateAddressRequest): Response<ApiResponse<AddressDto>>

    @PUT("addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: Long,
        @Body request: CreateAddressRequest
    ): Response<ApiResponse<AddressDto>>

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(@Path("id") addressId: Long): Response<ApiResponse<Unit>>

    @PUT("addresses/{id}/default")
    suspend fun setDefaultAddress(@Path("id") addressId: Long): Response<ApiResponse<AddressDto>>

    // ==================== Wishlist ====================

    @GET("wishlist")
    suspend fun getWishlist(): Response<ApiResponse<List<ProductDto>>>

    @POST("wishlist/{productId}")
    suspend fun addToWishlist(@Path("productId") productId: Long): Response<ApiResponse<Unit>>

    @DELETE("wishlist/{productId}")
    suspend fun removeFromWishlist(@Path("productId") productId: Long): Response<ApiResponse<Unit>>

    // ==================== Coupons ====================

    @GET("coupons/active")
    suspend fun getActiveCoupons(): Response<ApiResponse<List<CouponDto>>>

    @POST("coupons/validate")
    suspend fun validateCoupon(@Body request: ValidateCouponRequest): Response<ApiResponse<CouponDto>>

    // ==================== Reviews ====================

    @GET("products/{productId}/reviews")
    suspend fun getProductReviews(
        @Path("productId") productId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<ReviewDto>>>

    @POST("products/{productId}/reviews")
    suspend fun createReview(
        @Path("productId") productId: Long,
        @Body request: CreateReviewRequest
    ): Response<ApiResponse<ReviewDto>>

    // ==================== Payments ====================

    @POST("payments/create-order")
    suspend fun createPaymentOrder(@Body request: CreatePaymentOrderRequest): Response<ApiResponse<PaymentOrderResponse>>

    @POST("payments/verify")
    suspend fun verifyPayment(@Body request: VerifyPaymentRequest): Response<ApiResponse<PaymentVerificationResponse>>

    // ==================== Notifications ====================

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<NotificationDto>>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<Int>>

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") notificationId: Long): Response<ApiResponse<Unit>>
}

// Additional DTOs needed by the API

data class CouponDto(
    val id: Long = 0,
    val code: String = "",
    val description: String = "",
    val discountType: String = "PERCENTAGE",
    val discountValue: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val maxDiscount: Double = 0.0,
    val isActive: Boolean = true,
    val expiresAt: String? = null
) {
    fun toDomain() = com.sweetshop.customer.domain.model.Coupon(
        id = id,
        code = code,
        description = description,
        discountType = discountType,
        discountValue = discountValue,
        minOrderAmount = minOrderAmount,
        maxDiscount = maxDiscount,
        isActive = isActive,
        expiresAt = expiresAt
    )
}

data class ValidateCouponRequest(
    val code: String,
    val orderTotal: Double
)

data class ReviewDto(
    val id: Long = 0,
    val userId: Long = 0,
    val userName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: String? = null
)

data class CreateReviewRequest(
    val rating: Int,
    val comment: String
)

data class NotificationDto(
    val id: Long = 0,
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val isRead: Boolean = false,
    val createdAt: String? = null
)
