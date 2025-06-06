package com.example.shopko.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleStores(
    val id: Int,
    val type: String,
    val brand: String,
    val category: String,
    val unitSize: String,
    val price: String,
    val quantity: Int
) : Parcelable

fun Article.toArticleStores(): ArticleStores {
    return ArticleStores(
        id = this.id,
        type = this.type,
        brand = this.brand,
        category = this.category,
        unitSize = this.unitSize,
        price = this.price.toString(),
        quantity = this.quantity
    )
}
