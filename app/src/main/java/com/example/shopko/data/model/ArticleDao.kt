package com.example.shopko.data.model

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE storeBrand = :storeBrand")
    suspend fun getArticlesByBrand(storeBrand: String): List<ArticleEntity>
}