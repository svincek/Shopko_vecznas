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

@Database(entities = [StoreEntity::class, ArticleEntity::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_4_5 = object : Migration(4, 5){
                    override fun migrate(database: SupportSQLiteDatabase){}
                }
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "shopko_database"
                            ).addMigrations(MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
