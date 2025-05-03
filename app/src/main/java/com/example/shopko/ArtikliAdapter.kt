import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R

data class Artikl(val naziv: String, var kolicina: Int)

class ArtikliAdapter(private val artikli: MutableList<Artikl>) :
    RecyclerView.Adapter<ArtikliAdapter.ArtiklViewHolder>() {

    inner class ArtiklViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nazivArtikla: TextView = itemView.findViewById(R.id.naziv_artikla)
        val kolicina: TextView = itemView.findViewById(R.id.kolicina)
        val gumbMinus: ImageButton = itemView.findViewById(R.id.gumb_minus)
        val gumbPlus: ImageButton = itemView.findViewById(R.id.gumb_plus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtiklViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artikl, parent, false)
        return ArtiklViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtiklViewHolder, position: Int) {
        val trenutniArtikl = artikli[position]
        holder.nazivArtikla.text = trenutniArtikl.naziv
        holder.kolicina.text = trenutniArtikl.kolicina.toString()

        // Postavljanje funkcionalnosti za gumbove plus/minus
        holder.gumbMinus.setOnClickListener {
            if (trenutniArtikl.kolicina > 0) {
                trenutniArtikl.kolicina--
                notifyItemChanged(position)
            }
        }

        holder.gumbPlus.setOnClickListener {
            trenutniArtikl.kolicina++
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = artikli.size

    fun updatePopis(noviArtikli: List<Artikl>) {
        artikli.clear()
        artikli.addAll(noviArtikli)
        notifyDataSetChanged()
    }
}