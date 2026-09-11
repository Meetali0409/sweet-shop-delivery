package com.sweetshop.customer.di

import android.content.Context
import androidx.room.Room
import com.sweetshop.customer.data.local.SweetShopDatabase
import com.sweetshop.customer.data.local.dao.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SweetShopDatabase {
        return Room.databaseBuilder(
            context,
            SweetShopDatabase::class.java,
            "sweet_shop_db"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: SweetShopDatabase): CartDao {
        return database.cartDao()
    }
}
