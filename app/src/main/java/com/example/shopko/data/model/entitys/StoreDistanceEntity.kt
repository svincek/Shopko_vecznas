package com.example.shopko.data.model.entitys

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "store_distance",
    primaryKeys = ["storeIdA", "storeIdB"]
)
data class StoreDistanceEntity(
    val storeIdA: String,
    val storeIdB: String,
    val distance: Float
)
