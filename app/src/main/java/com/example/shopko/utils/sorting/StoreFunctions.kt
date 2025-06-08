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

    val sortedStores = userLatLng?.let { userLatLng ->
        stores.sortedBy { store ->
            DistanceUtil.calculateDistance(userLatLng, LatLng(store.latitude ?: 0.0, store.longitude ?: 0.0))
        }
    } ?: stores

    val limitedStores = sortedStores
        .groupBy { it.name }
        .flatMap { (_, storesInChain) ->
            storesInChain.take(3)
        }.take(12)

    val articlesByStore: Map<String, List<ArticleEntity>> = limitedStores.associate { store ->
        store.name to db.articleDao().getArticlesByStore(store.name)
    }

    val allStoreCombos = (1..maxCombos).flatMap { count ->
        limitedStores.combinations(count)
    }

    val validCombos = allStoreCombos.map { storeCombo ->
        val comboArticles = storeCombo.flatMap { store ->
            articlesByStore[store.name].orEmpty()
        }

        val typeToArticle: Map<String?, ArticleEntity?> = comboArticles
            .groupBy { it.subcategory }
            .mapValues { (_, articles) ->
                articles.find { it.isFavourite } ?: articles.minByOrNull { it.price }
            }

        val matchedArticles = articleList.mapNotNull { userArticle ->
            typeToArticle[userArticle.subcategory]?.copy(buyQuantity = userArticle.buyQuantity)
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
            compareByDescending<StoreComboResult> { it.matchedArticles.size }
                .thenBy { it.missingTypes.size }
                .thenBy { it.totalPrice }
                .thenBy { it.distance }
        )

        Filters.BYDISTANCE -> validCombos.sortedWith(
            compareByDescending<StoreComboResult> { it.matchedArticles.size }
                .thenBy { it.missingTypes.size }
                .thenBy { it.distance }
                .thenBy { it.totalPrice }
        )

        Filters.BYPRICE_DESC -> validCombos.sortedWith(
            compareByDescending<StoreComboResult> { it.matchedArticles.size }
                .thenBy { it.missingTypes.size }
                .thenByDescending { it.totalPrice }
        )

        Filters.DEFAULT -> validCombos.sortedByDescending { it.matchedArticles.size }
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



