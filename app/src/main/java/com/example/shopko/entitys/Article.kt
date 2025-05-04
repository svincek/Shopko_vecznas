package com.example.shopko.entitys;
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    @SerialName ("article_id") var id: Int,
    var type: String,
    var brand: String,
    var category: String,
    var unit_size: String,
    var price: Double,
    var quantity: Int = 1
)

@Serializable
data class ArticleList(
    val articles: List<Article>
)
