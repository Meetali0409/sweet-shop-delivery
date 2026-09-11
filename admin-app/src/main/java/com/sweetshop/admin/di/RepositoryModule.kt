package com.sweetshop.admin.di

import com.sweetshop.admin.data.repository.AuthRepositoryImpl
import com.sweetshop.admin.data.repository.CategoryRepositoryImpl
import com.sweetshop.admin.data.repository.CouponRepositoryImpl
import com.sweetshop.admin.data.repository.CustomerRepositoryImpl
import com.sweetshop.admin.data.repository.InventoryRepositoryImpl
import com.sweetshop.admin.data.repository.OrderRepositoryImpl
import com.sweetshop.admin.data.repository.ProductRepositoryImpl
import com.sweetshop.admin.data.repository.ReportRepositoryImpl
import com.sweetshop.admin.domain.repository.AuthRepository
import com.sweetshop.admin.domain.repository.CategoryRepository
import com.sweetshop.admin.domain.repository.CouponRepository
import com.sweetshop.admin.domain.repository.CustomerRepository
import com.sweetshop.admin.domain.repository.InventoryRepository
import com.sweetshop.admin.domain.repository.OrderRepository
import com.sweetshop.admin.domain.repository.ProductRepository
import com.sweetshop.admin.domain.repository.ReportRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(impl: CustomerRepositoryImpl): CustomerRepository

    @Binds
    @Singleton
    abstract fun bindCouponRepository(impl: CouponRepositoryImpl): CouponRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(impl: InventoryRepositoryImpl): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(impl: ReportRepositoryImpl): ReportRepository
}
