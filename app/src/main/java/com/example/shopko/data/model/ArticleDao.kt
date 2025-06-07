package com.example.shopko.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>): List<Long>

    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE storeBrand = :storeBrand")
    suspend fun getArticlesByStore(storeBrand: String): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE isFavourite = 1")
    suspend fun getFavouriteArticles(): List<ArticleEntity>

    @Query("SELECT DISTINCT category FROM articles")
    suspend fun getAllCategories(): List<String>

    @Query("UPDATE articles SET isFavourite = :isFavourite WHERE barcode = :barcode")
    suspend fun updateFavouriteStatus(barcode: String, isFavourite: Boolean): Int

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getArticlesCount(): Int
}
