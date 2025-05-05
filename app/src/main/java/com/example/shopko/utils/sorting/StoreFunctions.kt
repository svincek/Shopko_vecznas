package com.example.shopko.utils.sorting

import com.example.shopko.data.model.Article
import com.example.shopko.data.model.ShopkoApp
import com.example.shopko.data.model.Store
import com.example.shopko.data.model.StoreComboResult
import com.example.shopko.data.repository.getStores
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

suspend fun sortStoreCombo(articleList: List<Article>, maxCombos: Int, filter: Filters): List<StoreComboResult>{
    val stores = getStores()

    val allStoreCombos = (1..maxCombos).flatMap { count ->
        stores.combinations(count)
    }

    val context = ShopkoApp.getAppContext()
    val locationHelper = LocationHelper(context)
    val userLocation = locationHelper.getLastLocationSuspend()

    val userLatLng = userLocation?.let { LatLng(it.latitude, it.longitude) }

    val validCombos = allStoreCombos.map { storeCombo ->
        val allArticles = storeCombo.flatMap { it.articles }

        val typeToArticle = allArticles
            .groupBy { it.type }
            .mapValues { entry -> entry.value.minByOrNull { it.price } }

        val matchedArticles = articleList.mapNotNull { userArticle ->
            typeToArticle[userArticle.type]?.copy(quantity = userArticle.quantity)
        }

        val matchedTypes = matchedArticles.map { it.type }.toSet()
        val missingTypes = articleList.map{ it.type }.filterNot {it in matchedTypes}

        val totalPrice = matchedArticles.sumOf { it.price * it.quantity }

        val distance = userLatLng?.let {
            calculateComboDistance(it, storeCombo)
        } ?: Float.MAX_VALUE

        StoreComboResult(
            store = storeCombo,
            matchedArticles = matchedArticles,
            missingTypes = missingTypes,
            totalPrice = totalPrice,
            distance = distance
        )
    }

    return when(filter) {
        Filters.BYPRICE -> validCombos.sortedWith (
            compareBy <StoreComboResult> { it.missingTypes.size }
                .thenBy { it.totalPrice }
        )
        Filters.BYDISTANCE -> validCombos.sortedWith (
            compareBy <StoreComboResult> { it.missingTypes.size }
                .thenBy { it.distance }
        )
    }
}

fun calculateComboDistance(start: LatLng, stores: List<Store>): Float {
    val remaining = stores.toMutableList()
    var current = start
    var totalDistance = 0f

    while (remaining.isNotEmpty()) {
        val next = remaining.minByOrNull { store ->
            DistanceUtil.calculateDistance(current, store.latLngLoc)
        }!!

        totalDistance += DistanceUtil.calculateDistance(current, next.latLngLoc)
        current = next.latLngLoc
        remaining.remove(next)
    }

    return totalDistance
}