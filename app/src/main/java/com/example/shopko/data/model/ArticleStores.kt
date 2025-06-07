package com.example.shopko.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleStores(
    val id: Long?,
    val type: String,
    val brand: String,
    val category: String,
    val unitSize: String,
    val price: String,
    val quantity: Int
) : Parcelable

fun ArticleEntity.toArticleStores(): ArticleStores {
    return ArticleStores(
        id = this.productId,
        type = this.name.toString(),
        brand = this.brand.toString(),
        category = this.category.toString(),
        unitSize = this.quantity.toString(),
        price = this.price.toString(),
        quantity = this.buyQuantity
    )
}
