package com.example.shopko.data.model

import android.app.Application
import android.content.Context
import com.example.shopko.data.repository.AppDatabase
import com.example.shopko.utils.database.FirestoreSyncHelper

class ShopkoApp : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: ShopkoApp? = null

        fun getAppContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        val db = AppDatabase.getDatabase(this)
        val syncHelper = FirestoreSyncHelper(db)
        syncHelper.syncStores()
    }
}