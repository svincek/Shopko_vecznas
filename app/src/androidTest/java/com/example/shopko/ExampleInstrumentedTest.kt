package com.example.shopko

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shopko.data.model.dtos.ArticleDTO
import com.example.shopko.data.model.entitys.PaginatedResponse
import com.example.shopko.data.model.dtos.StoreDTO
import com.example.shopko.data.remote.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val client = KtorClient.httpClient
    private val baseUrl = "https://shopkobe.onrender.com"

    @Test
    suspend fun getArticlesPaginated(page: Int, size: Int): List<ArticleDTO> {
        val response: PaginatedResponse<ArticleDTO> = client.get("$baseUrl/articles") {
            parameter("page", page)
            parameter("size", size)
        }.body()
        return response.content
    }

    @Test
    suspend fun getStoresPaginated(page: Int, size: Int): List<StoreDTO> {
        val response: PaginatedResponse<StoreDTO> = client.get("$baseUrl/stores") {
            parameter("page", page)
            parameter("size", size)
        }.body()
        return response.content
    }
}