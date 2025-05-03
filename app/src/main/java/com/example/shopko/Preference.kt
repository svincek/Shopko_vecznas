package com.example.shopko

import Artikl
import ArtikliAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreferenceFragment : Fragment() {

    private lateinit var popisRecyclerView: RecyclerView
    private lateinit var artikliAdapter: ArtikliAdapter

    private val artikliList = mutableListOf(
        Artikl("Jabuka", 0),
        Artikl("Kruh", 0),
        Artikl("Mlijeko", 0)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_preference, container, false)

        // Inicijalizacija pogleda
        popisRecyclerView = view.findViewById(R.id.popis)
        val gumbDodaj: ImageButton = view.findViewById(R.id.gumb_dodaj)

        // Postavljanje RecyclerViewa
        artikliAdapter = ArtikliAdapter(artikliList)
        popisRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        popisRecyclerView.adapter = artikliAdapter

        // Dodavanje novih artikala
        gumbDodaj.setOnClickListener {
            prikaziDialogZaDodavanje()
        }

        return view
    }

    private fun prikaziDialogZaDodavanje() {
        val input = EditText(requireContext())
        input.hint = "Unesite naziv artikla"

        val alertDialog = android.app.AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Dodaj artikl")
        alertDialog.setView(input)

        alertDialog.setPositiveButton("Dodaj") { _, _ ->
            val noviArtikl = input.text.toString().trim()
            if (noviArtikl.isNotEmpty()) {
                artikliList.add(Artikl(noviArtikl, 0)) // Dodaj s početnom količinom 0
                artikliAdapter.notifyDataSetChanged()
            }
        }
        alertDialog.setNegativeButton("Odustani", null)
        alertDialog.show()
    }
}