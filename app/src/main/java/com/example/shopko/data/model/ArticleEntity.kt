package com.example.shopko.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val productId: String,
    val barcode: String?,
    val name: String,
    val brand: String,
    val category: String,
    val unit: String,
    val quantity: String,
    val price: Double,
    val unitPrice: Double,
    val bestPrice30: Double,
    val anchorPrice: Double,
    val specialPrice: Double,
    val storeBrand: String
)