package com.example.shopko

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.shopko.entitys.StoreComboMatchResult
import com.example.shopko.enums.Filters
import com.example.shopko.utils.dataFunctions.sortStoreCombo
import com.example.shopko.utils.location.LocationHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val articles = mutableListOf<String>() // List to store articles
    private lateinit var poveznica: ArrayAdapter<String> // Adapter for the ListView
    private lateinit var googleMap: GoogleMap
    private lateinit var locationHelper: LocationHelper
    private val popisExample: List<String> = listOf("SIR GAUDA", "JOGURT", "MLIJEKO - SVJEŽE", "ULJE SUNCOKRETOVO", "KRUH BIJELI")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            Toast.makeText(this, "Potrebna je dozvola za lokaciju", Toast.LENGTH_SHORT).show()
        }

        locationHelper = LocationHelper(this)

        val articleInput: EditText = findViewById(R.id.articleInput)
        val addButton: Button = findViewById(R.id.addButton)
        val articleList: ListView = findViewById(R.id.articleList)

        poveznica = ArrayAdapter(this, android.R.layout.simple_list_item_1, articles)
        articleList.adapter = poveznica

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        lifecycleScope.launch{
            val stores: List<StoreComboMatchResult> = sortStoreCombo(popisExample, 2, Filters.BYDISTANCE)

            articles.add(stores.first().toString())
            articles.add(stores.last().toString())

            poveznica.notifyDataSetChanged()
        }


        // Handle click events for the Add button
        addButton.setOnClickListener {

            val article = articleInput.text.toString().trim() // Get text from input

            if (article.isNotEmpty()) { // Check if input is not empty
                articles.add(article) // Add article to the list
                poveznica.notifyDataSetChanged() // Notify adapter about data change
                articleInput.text.clear() // Clear the input text
            } else {
                Toast.makeText(this, "Please enter a valid article", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMapReady(map: GoogleMap) {

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (::googleMap.isInitialized) {
                onMapReady(googleMap)
            }
        }
    }
}