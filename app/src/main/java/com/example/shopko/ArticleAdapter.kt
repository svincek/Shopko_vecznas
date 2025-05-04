
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.entitys.Article
import com.example.shopko.entitys.UserArticleList.articleList

class ArticleAdapter(private val article: MutableList<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleName: TextView = itemView.findViewById(R.id.article_name)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val buttonMinus: ImageButton = itemView.findViewById(R.id.button_minus)
        val buttonPlus: ImageButton = itemView.findViewById(R.id.button_plus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val trenutniArtikl = article[position]
        holder.articleName.text = trenutniArtikl.type
        holder.quantity.text = trenutniArtikl.quantity.toString()

        holder.buttonMinus.setOnClickListener {
            if (trenutniArtikl.quantity > 0) {
                trenutniArtikl.quantity--
                notifyItemChanged(position)
                articleList.filter{trenutniArtikl.type==it.type}.forEach {
                    it.quantity = trenutniArtikl.quantity
                }
            }
        }

        holder.buttonPlus.setOnClickListener {
            trenutniArtikl.quantity++
            notifyItemChanged(position)
            articleList.filter{trenutniArtikl.type==it.type}.forEach {
                it.quantity = trenutniArtikl.quantity
            }
        }
    }

    override fun getItemCount(): Int = article.size
}