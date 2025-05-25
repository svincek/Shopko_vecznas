package com.example.shopko.data.model

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(stores: List<StoreEntity>)

    @Query("SELECT name FROM stores")
    suspend fun getAllStoreBrands(): List<String>

    @Query("SELECT * FROM stores WHERE name = :brandName")
    suspend fun getStoresByBrand(brandName: String): List<StoreEntity>
}