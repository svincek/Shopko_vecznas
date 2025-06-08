package com.example.shopko.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>): List<Long>

    @Query("""
    SELECT * FROM articles
    WHERE category LIKE '%' || :query || '%' COLLATE NOCASE
    AND category NOT IN ('Sve', '')
    AND subcategory NOT IN ('Sve', '')
""")
    suspend fun getArticlesByCategoryContains(query: String): List<ArticleEntity>

    @Query("""
    SELECT * FROM articles
    WHERE subcategory = :subcategory
    AND brand NOT IN ('Nan', 'NaN')
""")
    suspend fun getArticlesBySubcategoryContains(subcategory: String): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE storeBrand = :storeBrand")
    suspend fun getArticlesByStore(storeBrand: String): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE isFavourite = 1")
    suspend fun getFavouriteArticles(): List<ArticleEntity>

    @Query("SELECT DISTINCT category FROM articles")
    suspend fun getAllCategories(): List<String>

    @Query("UPDATE articles SET isFavourite = :isFavourite WHERE brand = :brand " +
            "AND subcategory = :subcategory " +
            "AND quantity = :quantity ")
    suspend fun updateFavouriteStatus(brand: String, subcategory: String, quantity: String, isFavourite: Boolean): Int

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getArticlesCount(): Int
}
