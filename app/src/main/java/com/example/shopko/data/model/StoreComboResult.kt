package com.example.shopko.data.model

data class StoreComboResult (
    val store: List<Store>,
    val matchedArticles: List<Article>,
    val missingTypes: List<String>,
    val totalPrice: Double,
    val distance: Float,
)