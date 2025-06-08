package com.example.shopko.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_articles")
data class UserArticlesEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val brand: String?,
    val quantity: String?,
    val subcategory: String?,
    val buyQuantity: Int,
){
    fun toDisplay() = ArticleDisplay(
        brand = brand,
        quantity = quantity,
        subcategory = subcategory,
        buyQuantity = buyQuantity,
    )
}