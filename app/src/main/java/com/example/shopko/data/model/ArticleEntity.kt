package com.example.shopko.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val productId: Long?,
    val barcode: String?,
    val name: String?,
    val brand: String?,
    val category: String?,
    val subcategory: String?,
    val unit: String?,
    val quantity: String?,
    val price: Double,
    val unitPrice: Double?,
    val bestPrice: Double?,
    val anchorPrice: Double?,
    val specialPrice: Double?,
    val storeBrand: String?,
    var isFavourite: Boolean = false,
    var buyQuantity: Int = 1,
    var isChecked: Boolean = false
) : Parcelable