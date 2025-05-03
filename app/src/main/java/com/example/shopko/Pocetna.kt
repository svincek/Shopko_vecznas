package com.example.shopko

import Artikl
import ArtikliAdapter
import MyCustomDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.example.shopko.R


class PocetnaFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_pocetna, container, false)



        val scanButton = view.findViewById<View>(R.id.gumb_skeniraj)

        scanButton.setOnClickListener {
            MyCustomDialog().show(childFragmentManager, "MyCustomDialog")
        }



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
        val nastaviButton = view.findViewById<Button>(R.id.nastavi)
        nastaviButton.setOnClickListener {
            val intent = Intent(requireContext(), StoresActivity::class.java)
            startActivity(intent)
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
                    artikliList.add(Artikl(noviArtikl, 0)) // Add with default quantity
                    artikliAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Odustani", null)
            .create()
        alertDialog.show()
    }
}