package com.sweetshop.customer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sweetshop.customer.data.local.dao.CartDao
import com.sweetshop.customer.data.local.entity.CartItemEntity

@Database(
    entities = [CartItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SweetShopDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
