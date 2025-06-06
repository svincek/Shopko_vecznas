package com.example.shopko.data.preference

import android.content.Context
import android.content.SharedPreferences
import com.example.shopko.data.model.Article
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object PreferenceManager {
    private const val PREF_NAME = "article_preferences"

    fun getPreferences(context: Context, articleType: String): List<ArticlePreference>? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(articleType, null)
        return json?.let {
            try {
                Json.decodeFromString(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun addPreference(context: Context, articleType: String, newPref: ArticlePreference) {
        val currentPrefs = getPreferences(context, articleType)?.toMutableList() ?: mutableListOf()
        if (!currentPrefs.contains(newPref)) {
            currentPrefs.add(newPref)
            savePreferences(context, articleType, currentPrefs)
        }
    }

    fun removePreference(context: Context, articleType: String, toRemove: ArticlePreference) {
        val currentPrefs = getPreferences(context, articleType)?.toMutableList() ?: return
        currentPrefs.remove(toRemove)
        if (currentPrefs.isEmpty()) {
            clearPreference(context, articleType)
        } else {
            savePreferences(context, articleType, currentPrefs)
        }
    }

    private fun savePreferences(context: Context, articleType: String, prefsList: List<ArticlePreference>) {
        val json = Json.encodeToString(prefsList)
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(articleType, json).apply()
    }

    fun clearPreference(context: Context, articleType: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(articleType).apply()
    }

    fun clearAllPreferences(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
    fun getPreference(context: Context, articleType: String): ArticlePreference? {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(articleType, null)
        return json?.let {
            try {
                Json.decodeFromString<ArticlePreference>(it)
            } catch (e: Exception) {
                null
            }
        }
    }
    fun setMultiplePreferences(context: Context, articleType: String, preferences: List<ArticlePreference>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Json.encodeToString(preferences)
        prefs.edit().putString(articleType, json).apply()
    }

    fun getMultiplePreferences(context: Context, articleType: String): List<ArticlePreference> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(articleType, null) ?: return emptyList()
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
    fun getAllPreferences(context: Context): Map<String, List<ArticlePreference>> {
        val allPrefs = mutableMapOf<String, List<ArticlePreference>>()
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        for ((key, value) in sharedPrefs.all) {
            val prefs = getMultiplePreferences(context, key)
            if (prefs.isNotEmpty()) {
                allPrefs[key] = prefs
            }
        }

        return allPrefs
    }

}
