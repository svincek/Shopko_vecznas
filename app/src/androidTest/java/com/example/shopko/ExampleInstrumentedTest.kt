package com.example.shopko

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shopko.data.repository.getArticles
import com.example.shopko.data.repository.getStores
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun getArticles_readsAndParsesJsonCorrectly() {
        val articles = getArticles()
        assertTrue(articles.isNotEmpty())
        assertTrue(articles.any { it.type.contains("MLIJEKO") })
    }

    @Test
    fun getStores_parsesStoresAndMapsArticles() = runBlocking {
        val stores = getStores()
        assertTrue(stores.isNotEmpty())
        val first = stores.first()
        assertTrue(first.articles.isNotEmpty())
        assertNotNull(first.latLngLoc)
    }
}