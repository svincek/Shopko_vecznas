package com.example.shopko.data.model

class ArticleDisplay (
    val brand: String?,
    val subcategory: String?,
    var isFavourite: Boolean = false,
    var buyQuantity: Int = 1,
    var isChecked: Boolean = true
)