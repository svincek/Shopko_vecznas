package com.example.shopko.data.model

import SafeDoubleSerializer
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "articles")
data class ArticleEntity(
    @SerialName ("product_id") @PrimaryKey val productId: Long,
    val barcode: String?,
    val name: String,
    val brand: String,
    val category: String?,
    val subcategory: String?,
    val unit: String?,
    val quantity: String?,
    val price: Double,
    @Serializable(with = SafeDoubleSerializer::class)
    @SerialName ("unit_price") val unitPrice: Double?,
    @Serializable(with = SafeDoubleSerializer::class)
    @SerialName ("best_price") val bestPrice: Double?,
    @Serializable(with = SafeDoubleSerializer::class)
    @SerialName ("anchor_price") val anchorPrice: Double?,
    @Serializable(with = SafeDoubleSerializer::class)
    @SerialName ("special_price") val specialPrice: Double?,
    @SerialName ("store") val storeBrand: String,
    val isFavourite: Boolean = false
)