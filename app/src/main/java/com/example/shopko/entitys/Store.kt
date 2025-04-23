package com.example.shopko.entitys;
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

data class Store(
    var id: Int,
    var brand: String,
    var name: String,
    var opening_hours: String,
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
    var opening_hours: String,
    var location: String,
    var article_ids: List<Int>
)
