package com.sweetshop.admin.data.api

import com.sweetshop.admin.data.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AdminApi {

    // Config
    @GET("config")
    suspend fun getShopConfig(): Response<ApiResponse<ShopConfigDto>>

    @PUT("admin/config")
    suspend fun updateShopConfig(@Body request: UpdateShopConfigRequest): Response<ApiResponse<ShopConfigDto>>

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    // Dashboard
    @GET("admin/dashboard")
    suspend fun getDashboard(): Response<ApiResponse<DashboardDto>>

    @GET("admin/reports/sales-overview")
    suspend fun getSalesOverview(@Query("period") period: String): Response<ApiResponse<List<SalesReportDto>>>

    // Products
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null,
        @Query("category") categoryId: Long? = null
    ): Response<ApiResponse<PagedResponse<ProductListDto>>>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Long): Response<ApiResponse<ProductDto>>

    @POST("admin/products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ApiResponse<ProductDto>>

    @PUT("admin/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Long,
        @Body request: UpdateProductRequest
    ): Response<ApiResponse<ProductDto>>

    @DELETE("admin/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @PUT("admin/products/{id}/stock")
    suspend fun updateStock(
        @Path("id") id: Long,
        @Body request: UpdateStockRequest
    ): Response<ApiResponse<ProductDto>>

    // Categories
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>

    @POST("admin/categories")
    suspend fun createCategory(@Body request: CreateCategoryRequest): Response<ApiResponse<CategoryDto>>

    @PUT("admin/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Long,
        @Body request: UpdateCategoryRequest
    ): Response<ApiResponse<CategoryDto>>

    @DELETE("admin/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long): Response<ApiResponse<Unit>>

    // Orders
    @GET("admin/orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<OrderListDto>>>

    @GET("admin/orders/{id}")
    suspend fun getOrder(@Path("id") id: Long): Response<ApiResponse<OrderDto>>

    @PATCH("admin/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Long,
        @Body request: UpdateOrderStatusRequest
    ): Response<ApiResponse<OrderDto>>

    // Customers
    @GET("admin/customers")
    suspend fun getCustomers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("search") search: String? = null
    ): Response<ApiResponse<PagedResponse<CustomerListDto>>>

    @GET("admin/customers/{id}")
    suspend fun getCustomer(@Path("id") id: Long): Response<ApiResponse<CustomerListDto>>

    @PATCH("admin/customers/{id}/toggle-status")
    suspend fun toggleCustomerStatus(@Path("id") id: Long): Response<ApiResponse<UserDto>>

    // Coupons
    @GET("admin/coupons")
    suspend fun getCoupons(): Response<ApiResponse<List<CouponDto>>>

    @POST("admin/coupons")
    suspend fun createCoupon(@Body request: CreateCouponRequest): Response<ApiResponse<CouponDto>>

    @PUT("admin/coupons/{id}")
    suspend fun updateCoupon(
        @Path("id") id: Long,
        @Body request: UpdateCouponRequest
    ): Response<ApiResponse<CouponDto>>

    @DELETE("admin/coupons/{id}")
    suspend fun deleteCoupon(@Path("id") id: Long): Response<ApiResponse<Unit>>

    // Inventory
    @GET("admin/inventory")
    suspend fun getInventory(): Response<ApiResponse<List<InventoryItemDto>>>

    @PUT("admin/inventory/{productId}/stock")
    suspend fun updateInventoryStock(
        @Path("productId") productId: Long,
        @Body request: UpdateStockRequest
    ): Response<ApiResponse<ProductDto>>

    // Reports
    @GET("admin/reports/sales")
    suspend fun getSalesReport(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<ApiResponse<SalesReportDto>>

    @GET("admin/reports/sales-overview")
    suspend fun getSalesOverviewByPeriod(
        @Query("period") period: String
    ): Response<ApiResponse<List<SalesReportDto>>>
}
