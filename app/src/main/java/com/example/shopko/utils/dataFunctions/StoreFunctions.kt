package com.example.shopko.utils.dataFunctions

import com.example.shopko.entitys.ShopkoApp
import com.example.shopko.entitys.Store
import com.example.shopko.entitys.StoreComboMatchResult
import com.example.shopko.entitys.StoreMatchResult
import com.example.shopko.enums.Filters
import com.example.shopko.utils.general.combinations
import com.example.shopko.utils.location.DistanceUtil
import com.example.shopko.utils.location.LocationHelper
import com.example.shopko.utils.repository.getStores
import com.google.android.gms.maps.model.LatLng

suspend fun cheapestStore(articleList: List<String>): List<StoreMatchResult> {
    val stores: List<StoreMatchResult> = getStores().map { store ->
        val matchedArticles = articleList.mapNotNull {type ->
            store.articles.filter { it.type == type }
                .minByOrNull { it.price }
        }

        val matchedTypes = matchedArticles.map { it.type }.toSet()
        val missingTypes = articleList.filterNot {it in matchedTypes}

        val totalPrice = matchedArticles.sumOf { it.price }

        val distance = distanceFromStore(store)

        StoreMatchResult(
            store = store,
            matchedArticles = matchedArticles,
            missingTypes = missingTypes,
            totalPrice = totalPrice,
            distance = distance
        )
    }.sortedWith (
        compareBy <StoreMatchResult> {it.missingTypes.size}
            .thenBy {it.totalPrice}
    )
    return if(stores.isNotEmpty())
        stores
    else
        emptyList()
}

suspend fun closestStore(articleList: List<String>, filter: Filters): List<StoreMatchResult>{
    var stores: List<StoreMatchResult> = getStores().map { store ->
        val matchedArticles = articleList.mapNotNull {type ->
            store.articles.filter { it.type == type }
                .minByOrNull { it.price }
        }

        val matchedTypes = matchedArticles.map { it.type }.toSet()
        val missingTypes = articleList.filterNot {it in matchedTypes}

        val totalPrice = matchedArticles.sumOf { it.price }

        val distance = distanceFromStore(store)

        when(filter) {
            Filters.BYPRICE -> TODO()
            Filters.BYDISTANCE -> TODO()
        }

        StoreMatchResult(
            store = store,
            matchedArticles = matchedArticles,
            missingTypes = missingTypes,
            totalPrice = totalPrice,
            distance
        )
    }.sortedWith (
        compareBy <StoreMatchResult> {it.missingTypes.size}
            .thenBy { it.distance }
    )
    return if(stores.isNotEmpty())
        stores
    else
        emptyList()
}

suspend fun cheapestStoreCombo(articleList: List<String>, maxCombos: Int): List<StoreComboMatchResult>{
    val stores = getStores()

    val allStoreCombos = (1..maxCombos).flatMap { count ->
        stores.combinations(count)
    }

    val validCombos = allStoreCombos.map { storeCombo ->
        val allArticles = storeCombo.flatMap { it.articles }

        val matchedArticles = articleList.mapNotNull { type ->
            allArticles.filter { it.type == type }
                .minByOrNull { it.price }
        }

        val matchedTypes = matchedArticles.map { it.type }.toSet()
        val missingTypes = articleList.filterNot {it in matchedTypes}

        val totalPrice = matchedArticles.sumOf { it.price }

        val distance = distanceFromStoreCombos(storeCombo)

        StoreComboMatchResult(
            store = storeCombo,
            matchedArticles = matchedArticles,
            missingTypes = missingTypes,
            totalPrice = totalPrice,
            distance = distance
        )
    }

    return validCombos.sortedWith (
        compareBy <StoreComboMatchResult> { it.missingTypes.size }
            .thenBy { it.totalPrice }
    )
}

suspend fun distanceFromStore(store: Store): Float?{
    val context = ShopkoApp.getAppContext()
    val locationHelper = LocationHelper(context)
    val userLocation = locationHelper.getLastLocationSuspend() ?: return null

    val distance = DistanceUtil.calculateDistance(LatLng(userLocation.latitude, userLocation.longitude), store.latLngLoc)

    return distance
}

suspend fun distanceFromStoreCombos(stores: List<Store>): Float{
    val context = ShopkoApp.getAppContext()
    val locationHelper = LocationHelper(context)
    val userLocation = locationHelper.getLastLocationSuspend() ?: return Float.MAX_VALUE

    val start = LatLng(userLocation.latitude, userLocation.longitude)
    val remaining = stores.toMutableList()
    var current = start
    var totalDistance = 0f

    while (remaining.isNotEmpty()) {
        val next = remaining.minByOrNull { store ->
            DistanceUtil.calculateDistance(current, store.latLngLoc)
        }!!

        val distanceToNext = DistanceUtil.calculateDistance(current, next.latLngLoc)
        totalDistance += distanceToNext
        current = next.latLngLoc
        remaining.remove(next)
    }

    return totalDistance
}