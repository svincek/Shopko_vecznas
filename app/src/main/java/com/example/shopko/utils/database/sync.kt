
import android.content.Context
import android.util.Log
import com.example.shopko.data.model.ArticleEntity
import com.example.shopko.data.model.StoreEntity
import com.example.shopko.data.remote.ApiService
import com.example.shopko.data.repository.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun syncDataFromApiToRoom(context: Context) = withContext(Dispatchers.IO) {
    val api = ApiService()
    val db = AppDatabase.getDatabase(context)

    val storeCount = db.storeDao().getStoresCount()
    val articleCount = db.articleDao().getArticlesCount()

    try {
        val pageSize = 100
        var currentPage = 0
        var totalFetched: Int
        if(storeCount==0){
            do {
                val stores = api.getStoresPaginated(currentPage, pageSize)
                totalFetched = stores.size
                val storeEntities = stores.map {
                    StoreEntity(
                        storeId = it.storeId,
                        name = it.name,
                        address = it.address,
                        type = it.type,
                        city = it.city,
                        zipcode = it.zipcode,
                        workTime = it.workTime,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }

                if (stores.isNotEmpty()) {
                    db.storeDao().insertStores(storeEntities)
                    Log.d("Unos", stores.toString())
                }

                currentPage++

                Log.d("API Stores Page", "Page $currentPage fetched ${stores.size} stores")
            } while (totalFetched == pageSize)
        } else {
            Log.d("Sync", "Stores already initialized, skipping store sync")
        }

        currentPage = 0
        if(articleCount==0){

            do {
                val articles = api.getArticlesPaginated(currentPage, pageSize)
                totalFetched = articles.size
                val articleEntities = articles.map {
                    ArticleEntity(
                        productId = it.product_id,
                        barcode = it.barcode,
                        name = it.name,
                        brand = it.brand,
                        category = it.category,
                        subcategory = it.subcategory,
                        unit = it.unit,
                        quantity = it.quantity,
                        price = it.price,
                        unitPrice = it.unit_price,
                        bestPrice = it.best_price,
                        anchorPrice = it.anchor_price,
                        specialPrice = it.special_price,
                        storeBrand = it.store
                    )
                }

                if (articles.isNotEmpty()) {
                    db.articleDao().insertArticles(articleEntities)
                    Log.d("Unos", articles.toString())
                }

                currentPage++

                Log.d("API Articles Page", "Page $currentPage fetched ${articles.size} articles")
            } while (totalFetched > 0)
        } else {
            Log.d("Sync", "Articles already initialized, skipping article sync")
        }

    } catch (e: Exception) {
        Log.e("Error", e.toString())
        e.printStackTrace()
    }
}
