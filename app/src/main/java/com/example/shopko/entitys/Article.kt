package com.example.shopko.entitys;
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Article(
    @SerialName ("article_id") var id: Int,
    var type: String,
    var brand: String,
    var category: String,
    var unit_size: String,
    var price: Double
)

@Serializable
data class ArticleList(
    val articles: List<Article>
)
