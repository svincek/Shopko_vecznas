package com.example.shopko.data.model

import SafeDoubleSerializer
import kotlinx.serialization.Serializable

@Serializable
data class StoreDTO(
    val storeId: String,
    val name: String,
    val type: String?,
    val address: String,
    val city: String,
    @Serializable(with = SafeDoubleSerializer::class)
    val zipcode: Double?,
    val workTime: String,
    val latitude: Double?,
    val longitude: Double?
)