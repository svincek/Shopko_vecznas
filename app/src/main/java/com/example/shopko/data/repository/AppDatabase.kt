package com.example.shopko.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.shopko.data.model.daos.ArticleDao
import com.example.shopko.data.model.entitys.ArticleEntity
import com.example.shopko.data.model.daos.StoreDao
import com.example.shopko.data.model.entitys.StoreEntity
import com.example.shopko.data.model.daos.UserArticlesDao
import com.example.shopko.data.model.entitys.UserArticlesEntity

@Database(
    entities = [
        StoreEntity::class,
        ArticleEntity::class,
        UserArticlesEntity::class,
       ],
    version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun articleDao(): ArticleDao
    abstract fun userArticlesDao(): UserArticlesDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_6_7 = object : Migration(6, 7){
                    override fun migrate(database: SupportSQLiteDatabase){}
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shopko_database"
                ).addMigrations(MIGRATION_6_7)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
