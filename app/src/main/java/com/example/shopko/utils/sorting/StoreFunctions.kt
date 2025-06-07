package com.example.shopko.utils.sorting

import android.content.Context
import com.example.shopko.data.model.ArticleDisplay
import com.example.shopko.data.model.ArticleEntity
import com.example.shopko.data.model.StoreComboResult
import com.example.shopko.data.model.StoreEntity
import com.example.shopko.data.repository.AppDatabase
import com.example.shopko.utils.enums.Filters
import com.example.shopko.utils.general.combinations
import com.example.shopko.utils.location.DistanceUtil
import com.example.shopko.utils.location.LocationHelper
import com.google.android.gms.maps.model.LatLng

/**
 * Vraća sortiranu listu kombinacija trgovina koje najbolje odgovaraju korisnikovom popisu artikala,
 * uzimajući u obzir cijenu i udaljenost, ovisno o odabranom filtru.
 *
 * @param articleList Lista artikala koje korisnik traži.
 * @param maxCombos Maksimalan broj trgovina u jednoj kombinaciji (npr. 1 = samo jedna trgovina, 2 = kombinacije od dvije itd.).
 * @param filter Kriterij sortiranja (po cijeni ili udaljenosti).
 * @return Lista objekata [StoreComboResult] sortirana prema broju pronađenih artikala i filteru.
 *
 * Funkcija koristi:
 * - sve moguće kombinacije trgovina do veličine [maxCombos]
 * - geolokaciju korisnika za izračun udaljenosti
 * - Levenshteinovu udaljenost za "fuzzy" prepoznavanje artikala
 *
 * Artikli se spajaju iz više trgovina, uzimajući najjeftinije verzije, a rezultat uključuje i artikle koji nisu pronađeni.
 */

suspend fun sortStoreCombo(
    context: Context,
    articleList: List<ArticleDisplay>,
    maxCombos: Int,
    filter: Filters
): List<StoreComboResult> {
    val db = AppDatabase.getDatabase(context)
    val stores = db.storeDao().getAllStores()

    val locationHelper = LocationHelper(context)
    val userLocation = locationHelper.getLastLocationSuspend()
    val userLatLng = userLocation?.let { LatLng(it.latitude, it.longitude) }

    val sortedStores = userLatLng?.let {
        stores.sortedBy { store ->
            DistanceUtil.calculateDistance(userLatLng, LatLng(store.latitude ?: 0.0, store.longitude ?: 0.0))
        }
    } ?: stores

    val limitedStores = sortedStores.take(20)

    val allStoreCombos = (1..maxCombos).flatMap { count ->
        limitedStores.combinations(count)
    }

    val validCombos = allStoreCombos.map { storeCombo ->
        val allArticles = storeCombo.flatMap { db.articleDao().getArticlesByStore(it.name.toString()) }

        val typeToArticle: Map<String?, ArticleEntity?> = allArticles
            .groupBy { it.subcategory }
            .mapValues { (_, articles) ->
                articles.find { it.isFavourite } ?: articles.minByOrNull { it.price }
            }

        val matchedArticles = articleList.mapNotNull { userArticle ->
            val article = typeToArticle[userArticle.subcategory]?.copy(buyQuantity = userArticle.buyQuantity)
            article
        }

        val matchedTypes = matchedArticles.map { it.subcategory }.toSet()
        val missingTypes = articleList.map { it.subcategory }.filterNot { it in matchedTypes }

        val totalPrice = matchedArticles.sumOf {
            val quantity = it.buyQuantity.takeIf { q -> q > 0 } ?: 1
            it.price * quantity
        }

        val distance = userLatLng?.let {
            calculateComboDistance(it, storeCombo)
        } ?: Float.MAX_VALUE

        StoreComboResult(
            store = storeCombo,
            matchedArticles = matchedArticles,
            missingTypes = missingTypes.filterNotNull(),
            totalPrice = totalPrice,
            distance = distance
        )
    }

    return when (filter) {
        Filters.BYPRICE -> validCombos.sortedWith(
            compareBy<StoreComboResult> { it.missingTypes.size }.thenBy { it.totalPrice }
        )

        Filters.BYDISTANCE -> validCombos.sortedWith(
            compareBy<StoreComboResult> { it.missingTypes.size }.thenBy { it.distance }
        )

        Filters.BYPRICE_DESC -> validCombos.sortedWith(
            compareBy<StoreComboResult> { it.missingTypes.size }.thenByDescending { it.totalPrice }
        )

        Filters.DEFAULT -> validCombos.sortedBy { it.missingTypes.size }
    }
}


fun calculateComboDistance(start: LatLng, stores: List<StoreEntity>): Float {
    val validStores = stores.filter { it.latitude != null && it.longitude != null }

    val remaining = validStores.toMutableList()
    var current = start
    var totalDistance = 0f

    while (remaining.isNotEmpty()) {
        val next = remaining.minByOrNull { store ->
            DistanceUtil.calculateDistance(current, LatLng(store.latitude!!, store.longitude!!))
        }!!

        totalDistance += DistanceUtil.calculateDistance(current, LatLng(next.latitude!!, next.longitude!!))
        current = LatLng(next.latitude, next.longitude)
        remaining.remove(next)
    }

    return totalDistance
}


