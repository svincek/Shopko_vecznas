package com.example.shopko.data.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.shopko.data.model.ArticleDao
import com.example.shopko.data.model.ArticleEntity
import com.example.shopko.data.model.StoreDao
import com.example.shopko.data.model.StoreEntity

@Database(entities = [StoreEntity::class, ArticleEntity::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_5_6 = object : Migration(5, 6){
                    override fun migrate(database: SupportSQLiteDatabase){}
                }
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shopko_database"
                ).addMigrations(MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
