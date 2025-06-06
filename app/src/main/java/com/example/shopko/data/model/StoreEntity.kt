package com.example.shopko.data.model

import SafeDoubleSerializer
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val storeId: String,
    val name: String,
    val type: String?,
    val address: String,
    val city: String,
    @Serializable(with = SafeDoubleSerializer::class)
    val zipcode: Double?,
    val workTime: String
)