package com.example.shopko.data.repository

import android.util.Log
import com.example.shopko.data.model.Article
import com.example.shopko.data.model.ArticleList
import com.example.shopko.data.model.ShopkoApp
import com.example.shopko.data.model.Store
import com.example.shopko.data.model.StoreListJSON
import com.example.shopko.utils.location.LatLngFromAddress
import kotlinx.serialization.json.Json


fun getArticles(): List<Article> {
    val context = ShopkoApp.getAppContext()
    try {
        val jsonString = context.assets.open("shopko_articles.json").bufferedReader().use { it.readText() }
        return Json.decodeFromString<ArticleList>(jsonString).articles
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("Articles", "Failed to fetch articles", e)
    }
    return emptyList()
}

suspend fun getStores(): List<Store> {
    val context = ShopkoApp.getAppContext()
    try {
        val jsonString = context.assets.open("shopko_stores.json").bufferedReader().use { it.readText() }
        val jsonStores = Json.decodeFromString<StoreListJSON>(jsonString).stores
        return jsonStores.map { jsonStore ->
            var articles = mutableListOf<Article>()
            jsonStore.articleIds.forEach { articleId ->
                val article = getArticles().find {it.id == articleId}
                article?.let { articles.add(it) }
            }
            val latLngLoc = LatLngFromAddress.getLatLngFromAddressSuspend(context, jsonStore.location)
            Store(jsonStore.id, jsonStore.brand, jsonStore.name, jsonStore.openingHours, jsonStore.location, articles,
                latLngLoc!!
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("Stores", "Failed to fetch stores", e)
    }
    return emptyList()
}