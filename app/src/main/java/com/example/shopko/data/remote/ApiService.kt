package com.example.shopko.data.remote

import com.example.shopko.data.model.ArticleDTO
import com.example.shopko.data.model.PaginatedResponse
import com.example.shopko.data.model.StoreDTO
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ApiService {
    private val client = KtorClient.httpClient
    private val baseUrl = "https://shopkobe.onrender.com"

    suspend fun getArticlesPaginated(page: Int, size: Int): List<ArticleDTO> {
        val response: PaginatedResponse<ArticleDTO> = client.get("$baseUrl/articles") {
            parameter("page", page)
            parameter("size", size)
        }.body()
        return response.content
    }

    suspend fun getStoresPaginated(page: Int, size: Int): List<StoreDTO> {
        val response: PaginatedResponse<StoreDTO> = client.get("$baseUrl/stores") {
            parameter("page", page)
            parameter("size", size)
        }.body()
        return response.content
    }
}
