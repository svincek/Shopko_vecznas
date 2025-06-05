import android.content.Context
import com.example.shopko.data.model.ArticleEntity
import com.example.shopko.data.model.StoreEntity
import com.example.shopko.data.repository.AppDatabase
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

@Serializable
data class MongoArticle(
    val productId: Long,
    val barcode: String?,
    val name: String,
    val brand: String,
    val category: String?,
    val subcategory: String?,
    val unit: String?,
    val quantity: String?,
    val price: Double,
    val unitPrice: Double,
    val bestPrice: Double,
    val anchorPrice: Double,
    val specialPrice: Double?,
    val storeBrand: String,
    val isFavourite: Boolean = false
)

@Serializable
data class MongoStore(
    val storeId: Long,
    val address: String,
    val brand: String,
    val city: String,
    val name: String,
    val type: String,
    val zipcode: Double,
    val workTime: String,
)

suspend fun syncDataFromMongoToRoom(context: Context) = withContext(Dispatchers.IO) {
    val connectionString = ConnectionString("mongodb://simonvincek:Sajmonkecniv1@shopko.cot92bh.mongodb.net/?retryWrites=true&w=majority&appName=Shopko")
    val settings = MongoClientSettings.builder().applyConnectionString(connectionString).build()
    val client = KMongo.createClient(settings).coroutine
    val database = client.getDatabase("Shopko")

    val db = AppDatabase.getDatabase(context)

    val storeCollections = listOf("stores")
    val articleCollections = listOf(
        "articles_konzum",
                "articles_ktc",
                "articles_lidl",
                "articles_metro",
                "articles_ntl",
                "articles_plodine",
                "articles_ribola",
                "articles_spar",
                "articles_studenac",
                "articles_tommy",
                "articles_trgocentar"
    )

    for (collectionName in storeCollections) {
        val collection = database.getCollection<MongoStore>(collectionName)
        val stores = collection.find().toList().map {
            StoreEntity(
                storeId = it.storeId,
                name = it.name,
                address = it.address,
                type = it.type,
                city = it.city,
                zipcode = it.zipcode,
                workTime = it.workTime,
            )
        }
        db.storeDao().insertStores(stores)
    }

    for (collectionName in articleCollections) {
        val collection = database.getCollection<MongoArticle>(collectionName)
        val articles = collection.find().toList().map {
            ArticleEntity(
                productId = it.productId,
                barcode = it.barcode,
                name = it.name,
                brand = it.brand,
                category = it.category,
                subcategory = it.subcategory,
                unit = it.unit,
                quantity = it.quantity,
                storeBrand = it.storeBrand,
                isFavourite = false,
                price = it.price,
                unitPrice = it.unitPrice,
                bestPrice = it.bestPrice,
                anchorPrice = it.anchorPrice,
                specialPrice = it.specialPrice
            )
        }
        db.articleDao().insertArticles(articles)
    }

    client.close()
}