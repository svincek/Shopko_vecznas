package com.example.shopko.entitys

import android.app.Activity
import android.app.Application
import android.content.Context

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
}
