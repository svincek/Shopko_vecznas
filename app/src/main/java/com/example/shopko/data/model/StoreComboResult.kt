package com.example.shopko.data.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize



data class StoreComboResult (
val store: List<StoreEntity>,
val matchedArticles: List<ArticleEntity>,
val missingTypes: List<String>,
val totalPrice: Double,
val distance: Float,
)


@Parcelize
data class StoreComboResultParcelable(
    val storeName: String,
    val storeLocation: String,
    val storeHours: String,
    val articles: List<ArticleStores>,
    val totalPrice: Double
) : Parcelable
