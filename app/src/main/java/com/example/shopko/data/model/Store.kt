package com.example.shopko.data.model
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Store(
    var id: Int,
    var brand: String,
    var name: String,
    var openingHours: String,
    var location: String,
    var articles: List<Article>,
    var latLngLoc: LatLng
)

@Serializable
data class StoreListJSON(
    val stores: List<StoreJSON>
)

@Serializable
data class StoreJSON(
    @SerialName("store_id") var id: Int,
    var brand: String,
    var name: String,
    @SerialName("opening_hours") var openingHours: String,
    var location: String,
    @SerialName("article_ids") var articleIds: List<Int>
)
