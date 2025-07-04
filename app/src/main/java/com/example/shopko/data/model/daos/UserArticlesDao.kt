package com.example.shopko.data.model.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shopko.data.model.entitys.ArticleDisplay
import com.example.shopko.data.model.entitys.UserArticlesEntity

@Dao
interface UserArticlesDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(articles: List<UserArticlesEntity>)

    @Query("SELECT * FROM user_articles")
    suspend fun getAll(): List<UserArticlesEntity>

    @Query("DELETE FROM user_articles")
    suspend fun clearAll()

    fun UserArticlesEntity.toDisplay() = ArticleDisplay(
        brand = brand,
        quantity = quantity,
        subcategory = subcategory,
        buyQuantity = buyQuantity,
    )
}