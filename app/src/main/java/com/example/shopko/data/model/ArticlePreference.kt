package com.example.shopko.data.preference

import kotlinx.serialization.Serializable

@Serializable
data class ArticlePreference(
    val brand: String,
    val unitSize: String
)
