package com.example.shopko.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(stores: List<StoreEntity>): List<Long>

    @Query("SELECT name FROM stores")
    suspend fun getAllStoreBrands(): List<String>

    @Query("SELECT * FROM stores WHERE name = :brandName")
    suspend fun getStoresByBrand(brandName: String): List<StoreEntity>

    @Query("UPDATE stores SET workTime = :workTime WHERE name = :storeName")
    suspend fun updateStoreWorkTime(storeName: String, workTime: String): Int
}
