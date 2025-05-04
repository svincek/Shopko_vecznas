package com.example.shopko

import com.example.shopko.data.model.Article
import com.example.shopko.data.model.Store
import com.example.shopko.utils.enums.Filters
import com.google.android.gms.maps.model.LatLng
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val articleMilk = Article(1, "MLIJEKO - SVJEŽE", "DUKAT", "MLIJEČNI PROIZVODI", "1L", 1.37, 2)
    private val articleCheese = Article(2, "SIR", "VINDIJA", "MLIJEČNI PROIZVODI", "500g", 3.5, 1)

    private val store1 = Store(
        id = 1,
        brand = "KONZUM",
        name = "Konzum - Trešnjevka",
        openingHours = "7:00-21:00",
        location = "Trešnjevački trg 1",
        articles = listOf(
            articleMilk.copy(price = 1.30),  // cheaper milk
            articleCheese.copy(price = 3.6)
        ),
        latLngLoc = LatLng(45.8, 15.9)
    )

    private val store2 = Store(
        id = 2,
        brand = "LIDL",
        name = "Lidl - Ljubljanica",
        openingHours = "7:00-21:00",
        location = "Ozaljska 148",
        articles = listOf(
            articleMilk.copy(price = 1.50),
            articleCheese.copy(price = 3.4)  // cheaper cheese
        ),
        latLngLoc = LatLng(45.81, 15.91)
    )

    @Test
    fun `test combo sorted by price correctly`() {
        val userLatLng = LatLng(45.1, 16.1)
        val fakeDistanceCalculator = DistanceCalculator { _, _ -> 10f }
        val results = sortStoreComboTestable(
            articleList = listOf(articleMilk, articleCheese),
            stores = listOf(store1, store2),
            userLatLng = userLatLng,
            maxCombos = 1,
            filter = Filters.BYPRICE,
            distanceCalculator = fakeDistanceCalculator
        )
        assertEquals(2, results.size)
        assertEquals(1, results.first().store.first().id) // Lidl has cheaper combo
    }

    @Test
    fun `test combo sorted by distance correctly`() {
        val fakeDistanceCalculator = DistanceCalculator { _, _ -> 10f }
        val results = sortStoreComboTestable(
            articleList = listOf(articleMilk, articleCheese),
            stores = listOf(store1, store2),
            userLatLng = LatLng(45.805, 15.905), // closer to Konzum
            maxCombos = 1,
            filter = Filters.BYDISTANCE,
            distanceCalculator = fakeDistanceCalculator
        )
        assertEquals(1, results.first().store.first().id)
    }

    @Test
    fun `test quantity is preserved`() {
        val article = Article(1, "MLIJEKO - SVJEŽE", "DUKAT", "MLIJEČNI", "1L", 1.3, quantity = 4)
        val userLatLng = LatLng(45.1, 16.1)
        val fakeDistanceCalculator = DistanceCalculator { _, _ -> 10f }
        val results = sortStoreComboTestable(
            articleList = listOf(article),
            stores = listOf(store1),
            userLatLng = userLatLng,
            maxCombos = 1,
            filter = Filters.BYPRICE,
            distanceCalculator = fakeDistanceCalculator
        )
        assertEquals(4, results.first().matchedArticles.first().quantity)
        assertEquals(4, results.first().matchedArticles.first().quantity)
    }

    @Test
    fun `test handles missing articles`() {
        val newArticle = articleMilk.copy(type = "SOK OD JABUKE") // not in any store
        val userLatLng = LatLng(45.1, 16.1)
        val fakeDistanceCalculator = DistanceCalculator { _, _ -> 10f }
        val results = sortStoreComboTestable(
            articleList = listOf(newArticle),
            stores = listOf(store1, store2),
            userLatLng = userLatLng,
            maxCombos = 1,
            filter = Filters.BYPRICE,
            distanceCalculator = fakeDistanceCalculator
        )
        assertEquals(2, results.size)
        assertTrue(results.first().missingTypes.contains("SOK OD JABUKE"))
    }

    @Test
    fun `test total price calculation is correct`() {
        val userLatLng = LatLng(45.1, 16.1)
        val fakeDistanceCalculator = DistanceCalculator { _, _ -> 10f }
        val results = sortStoreComboTestable(
            articleList = listOf(articleMilk, articleCheese),
            stores = listOf(store1),
            userLatLng = userLatLng,
            maxCombos = 1,
            filter = Filters.BYPRICE,
            distanceCalculator = fakeDistanceCalculator
        )
        val expectedPrice = 1.30 * 2 + 3.6 * 1
        assertEquals(expectedPrice, results.first().totalPrice, 0.01)
    }
}