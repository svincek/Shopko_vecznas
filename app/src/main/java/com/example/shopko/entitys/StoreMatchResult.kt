package com.example.shopko.entitys

data class StoreMatchResult (
    val store: Store,
    val matchedArticles: List<Article>,
    val missingTypes: List<String>,
    val totalPrice: Double,
    val distance: Float?
)
