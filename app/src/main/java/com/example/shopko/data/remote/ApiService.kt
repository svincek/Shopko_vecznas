package com.example.shopko.data.remote

import com.example.shopko.data.model.ArticleEntity
import com.example.shopko.data.model.PaginatedResponse
import com.example.shopko.data.model.StoreEntity
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface ArticleApiService {
    suspend fun getArticles(page: Int, size: Int): List<ArticleEntity>
}

class ApiService {
    private val client = KtorClient.httpClient
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun getArticles(): List<ArticleEntity> {
        return client.get("$baseUrl/articles").body()
    }

    suspend fun getArticlesPaginated(page: Int, size: Int): List<ArticleEntity> {
        val response: PaginatedResponse<ArticleEntity> = client.get("$baseUrl/articles") {
            parameter("page", page)
            parameter("size", size)
        }.body()
        return response.content
    }

    suspend fun postArticle(article: ArticleEntity): ArticleEntity {
        return client.post("$baseUrl/articles") {
            contentType(ContentType.Application.Json)
            setBody(article)
        }.body()
    }

    suspend fun getStores(): List<StoreEntity> {
        return client.get("$baseUrl/stores").body()
    }

    suspend fun getStoresPaginated(page: Int, size: Int): List<StoreEntity> {
        val response: PaginatedResponse<StoreEntity> = client.get("$baseUrl/articles") {
            parameter("page", page)
            parameter("size", size)
        }.body()
        return response.content
    }
}
