package com.example.shopko.data.model

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
    val zipcode: Double?,
    val workTime: String,
    val logoUrl: String?,
    val latitude: Double?,
    val longitude: Double?
)