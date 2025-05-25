package com.example.shopko.utils.database

import com.example.shopko.data.model.ArticleEntity
import com.example.shopko.data.model.StoreEntity
import com.example.shopko.data.repository.AppDatabase
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirestoreSyncHelper(private val db: AppDatabase) {

    fun syncStores() {
        Firebase.firestore.collection("stores")
            .get()
            .addOnSuccessListener { result ->
                val stores = result.documents.mapNotNull { doc ->
                    doc.toObject(StoreEntity::class.java)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    db.storeDao().insertStores(stores)
                }

                val uniqueBrands = stores.map { it.name }.distinct()

                for (brand in uniqueBrands) {
                    syncArticlesForBrand(brand)
                }
            }
    }

    fun syncArticlesForBrand(brand: String) {
        Firebase.firestore
            .collection("brands")
            .document(brand)
            .collection("articles")
            .get()
            .addOnSuccessListener { result ->
                val articles = result.documents.mapNotNull { doc ->
                    val article = doc.toObject(ArticleEntity::class.java)
                    article?.copy(storeBrand = brand)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    db.articleDao().insertArticles(articles)
                }
            }
    }
}

