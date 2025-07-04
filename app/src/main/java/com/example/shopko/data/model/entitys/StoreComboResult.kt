package com.example.shopko.data.model.entitys
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
    val storeNames: List<String>,
    val storeLocations: List<String>,
    val storeHours: List<String>,
    val articles: List<ArticleEntity>,
    val totalPrice: Double
) : Parcelable
