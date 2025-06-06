package com.example.shopko.data.model

import SafeDoubleSerializer
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDTO(
    @PrimaryKey val product_id: Long?,
    val barcode: String?, val name: String?,
    val brand: String?,
    val category: String?,
    val subcategory: String?,
    val unit: String?,
    val quantity: String?,
    val price: Double,
    @Serializable(with = SafeDoubleSerializer::class)
    val unit_price: Double?,
    @Serializable(with = SafeDoubleSerializer::class)
    val best_price: Double?,
    @Serializable(with = SafeDoubleSerializer::class)
    val anchor_price: Double?,
    @Serializable(with = SafeDoubleSerializer::class)
    val special_price: Double?,
    val store: String?
)