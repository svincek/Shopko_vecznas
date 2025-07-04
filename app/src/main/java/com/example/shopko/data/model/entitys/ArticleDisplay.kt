package com.example.shopko.data.model.entitys

data class ArticleDisplay (
    val brand: String? = "",
    val quantity: String? = "",
    val subcategory: String?,
    var isFavourite: Boolean = false,
    var buyQuantity: Int = 1,
    var isChecked: Boolean = true
){
    fun toEntity() = UserArticlesEntity(
        brand = brand,
        quantity = quantity,
        subcategory = subcategory,
        buyQuantity = buyQuantity,
    )
}