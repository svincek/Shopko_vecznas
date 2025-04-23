package com.example.shopko.utils.repository

import com.example.shopko.entitys.Article
import com.example.shopko.entitys.ArticleList
import com.example.shopko.entitys.ShopkoApp
import com.example.shopko.entitys.Store
import kotlinx.serialization.json.Json
import com.example.shopko.entitys.StoreListJSON
import com.example.shopko.utils.location.LatLngFromAddress


fun getArticles(): List<Article> {
    val context = ShopkoApp.getAppContext()
    try {
        val jsonString = context.assets.open("shopko_articles.json").bufferedReader().use { it.readText() }
        return Json.decodeFromString<ArticleList>(jsonString).articles
    } catch (e: Exception) {
        e.printStackTrace()
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
            jsonStore.article_ids.forEach { articleId ->
                val article = getArticles().find {it.id == articleId}
                article?.let { articles.add(it) }
            }
            val latLngLoc = LatLngFromAddress.getLatLngFromAddressSuspend(context, jsonStore.location)
            Store(jsonStore.id, jsonStore.brand, jsonStore.name, jsonStore.opening_hours, jsonStore.location, articles,
                latLngLoc!!
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return emptyList()
}