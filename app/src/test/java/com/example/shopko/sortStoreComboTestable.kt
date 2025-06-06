package com.example.shopko

import com.example.shopko.data.model.Article
import com.example.shopko.data.model.Store
import com.example.shopko.data.model.StoreComboResult
import com.example.shopko.utils.enums.Filters
import com.example.shopko.utils.general.combinations
import com.google.android.gms.maps.model.LatLng

fun sortStoreComboTestable(
    articleList: List<Article>,
    stores: List<Store>,
    userLatLng: LatLng?,
    maxCombos: Int,
    filter: Filters,
    distanceCalculator: DistanceCalculator
): List<StoreComboResult> {
    val allStoreCombos = (1..maxCombos).flatMap { count ->
        stores.combinations(count)
    }

    val validCombos = allStoreCombos.map { storeCombo ->
        val allArticles = storeCombo.flatMap { it.articles }

        val typeToArticle = allArticles
            .groupBy { it.type }
            .mapValues { entry -> entry.value.minByOrNull { it.price } }

        val matchedArticles = articleList.mapNotNull { userArticle ->
            typeToArticle[userArticle.type]?.copy(quantity = userArticle.quantity)
        }

        val matchedTypes = matchedArticles.map { it.type }.toSet()
        val missingTypes = articleList.map { it.type }.filterNot { it in matchedTypes }

        val totalPrice = matchedArticles.sumOf { it.price * it.quantity }

        val distance = userLatLng?.let {
            calculateComboDistance(it, storeCombo, distanceCalculator)
        } ?: Float.MAX_VALUE

        StoreComboResult(
            store = storeCombo,
            matchedArticles = matchedArticles,
            missingTypes = missingTypes,
            totalPrice = totalPrice,
            distance = distance
        )
    }

    return when (filter) {
        Filters.BYPRICE -> validCombos.sortedWith(
            compareBy<StoreComboResult> { it.missingTypes.size }
                .thenBy { it.totalPrice }
        )
        Filters.BYDISTANCE -> validCombos.sortedWith(
            compareBy<StoreComboResult> { it.missingTypes.size }
                .thenBy { it.distance }
        )
        Filters.BYPRICE_DESC -> validCombos.sortedWith(
            compareBy<StoreComboResult> { it.missingTypes.size }
                .thenByDescending { it.totalPrice }
        )
        Filters.DEFAULT -> validCombos
    }
}

fun calculateComboDistance(start: LatLng, stores: List<Store>, distanceCalculator: DistanceCalculator): Float {
    val remaining = stores.toMutableList()
    var current = start
    var totalDistance = 0f

    while (remaining.isNotEmpty()) {
        val next = remaining.minByOrNull { store ->
            distanceCalculator.calculate(current, store.latLngLoc)
        }!!

        totalDistance += distanceCalculator.calculate(current, next.latLngLoc)
        current = next.latLngLoc
        remaining.remove(next)
    }

    return totalDistance
}

