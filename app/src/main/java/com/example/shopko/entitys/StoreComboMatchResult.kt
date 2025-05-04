package com.example.shopko.entitys

data class StoreComboMatchResult (
    val store: List<Store>,
    val matchedArticles: List<Article>,
    val missingTypes: List<String>,
    val totalPrice: Double,
    val distance: Float,
)
