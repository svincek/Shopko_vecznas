package com.example.shopko.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.shopko.R
import com.example.shopko.utils.location.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PocetnaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val articles = mutableListOf<String>() // List to store articles
    private lateinit var adapter: ArrayAdapter<String> // Adapter for the ListView
    private lateinit var locationHelper: LocationHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pocetna, container, false)

        // Find UI components
        val listView = view.findViewById<ListView>(R.id.articleList)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val inputArticle = view.findViewById<EditText>(R.id.articleInput)

        // Initialize map using SupportMapFragment
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this) // Load the map asynchronously

        // Initialize adapter for ListView
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, articles)
        listView.adapter = adapter

        // Add button logic
        addButton.setOnClickListener {
            val article = inputArticle.text.toString()
            if (article.isNotBlank()) {
                articles.add(article)
                adapter.notifyDataSetChanged()
                inputArticle.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please enter an article", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Initialize location helper
        locationHelper = LocationHelper(requireContext())

        // Request and enable user location if permissions are granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true

            // Example: Add a default marker on map load
            val defaultLocation = LatLng(37.7749, -122.4194) // San Francisco
            googleMap.addMarker(
                MarkerOptions().position(defaultLocation).title("Marker in San Francisco")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f)) // Zoom level
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            }
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}