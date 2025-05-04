package com.example.shopko

import ArtikliAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.entitys.Article
import com.example.shopko.entitys.UserArticleList.articleList
import com.example.shopko.utils.repository.getArticles

class PocetnaFragment : Fragment() {

    private lateinit var popisRecyclerView: RecyclerView
    private lateinit var artikliAdapter: ArtikliAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pocetna, container, false)

        // Inicijalizacija pogleda
        popisRecyclerView = view.findViewById(R.id.popis)
        val gumbDodaj: ImageButton = view.findViewById(R.id.gumb_dodaj)

        // Postavljanje RecyclerViewa
        artikliAdapter = ArtikliAdapter(articleList)
        popisRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        popisRecyclerView.adapter = artikliAdapter

        // Dodavanje novih artikala
        gumbDodaj.setOnClickListener {
            prikaziDialogZaDodavanje()
        }
        val nastaviButton = view.findViewById<Button>(R.id.nastavi)
        nastaviButton.setOnClickListener {
            if(articleList.isNotEmpty()){
                val intent = Intent(requireContext(), StoresActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(activity, "Popis je prazan!", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun prikaziDialogZaDodavanje() {
        val input = EditText(requireContext()).apply {
            hint = "Unesite naziv artikla"
            setPadding(32, 32, 32, 32)
        }

        val alertDialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Dodaj artikl")
            .setView(input)
            .setPositiveButton("Dodaj") { _, _ ->
                val noviArtikl = input.text.toString().trim()
                if (noviArtikl.isNotEmpty()) {
                    val artikl: Article? = getArticles().firstOrNull { it.type == noviArtikl }
                    if(artikl != null){
                        articleList.add(artikl)
                        artikliAdapter.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(activity, "Nije pronađen artikl: $noviArtikl", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Odustani", null)
            .create()
        alertDialog.show()
    }
}