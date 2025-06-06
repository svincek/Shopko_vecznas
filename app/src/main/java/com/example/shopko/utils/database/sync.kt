
import android.content.Context
import com.example.shopko.data.remote.ApiService
import com.example.shopko.data.repository.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun syncDataFromApiToRoom(context: Context) = withContext(Dispatchers.IO) {
    val api = ApiService()
    val db = AppDatabase.getDatabase(context)

    try {
        val pageSize = 50
        var currentPage = 0
        var totalFetched: Int
        do {
            val stores = api.getStoresPaginated(currentPage, pageSize)
            totalFetched = stores.size

            if (stores.isNotEmpty()) {
                db.storeDao().insertStores(stores)
            }

            currentPage++
        } while (totalFetched == pageSize)

        currentPage = 0
        do {
            val articles = api.getArticlesPaginated(currentPage, pageSize)
            totalFetched = articles.size

            if (articles.isNotEmpty()) {
                db.articleDao().insertArticles(articles)
            }

            currentPage++
        } while (totalFetched == pageSize)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
