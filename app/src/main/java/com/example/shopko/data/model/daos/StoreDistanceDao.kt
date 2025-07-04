package com.example.shopko.data.model.daos

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shopko.data.model.entitys.StoreDistanceEntity

interface StoreDistanceDao {

    @Query("SELECT * FROM store_distance WHERE (storeIdA = :id1 AND storeIdB = :id2) OR (storeIdA = :id2 AND storeIdB = :id1) LIMIT 1")
    suspend fun getDistance(id1: String, id2: String): StoreDistanceEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertDistance(distance: StoreDistanceEntity)
}