package com.example.shopko.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    @SerialName ("article_id") var id: Int,
    var type: String,
    var brand: String,
    var category: String,
    @SerialName ("unit_size") var unitSize: String,
    var price: Double,
    var quantity: Int = 1,
    var isChecked: Boolean = true
)

@Serializable
data class ArticleList(
    val articles: List<Article>
)
