package com.example.shopko

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.shopko.entitys.StoreComboMatchResult
import com.example.shopko.enums.Filters
import com.example.shopko.utils.camera.runTextRecognitionOnImage
import com.example.shopko.utils.dataFunctions.sortStoreCombo
import com.example.shopko.utils.location.LocationHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val articles = mutableListOf<String>() // List to store articles
    private lateinit var poveznica: ArrayAdapter<String> // Adapter for the ListView
    private lateinit var googleMap: GoogleMap
    private lateinit var locationHelper: LocationHelper
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.captureButton).setOnClickListener {
            takePhoto()
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            Toast.makeText(this, "Potrebna je dozvola za lokaciju", Toast.LENGTH_SHORT).show()
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                1001
            )
            Toast.makeText(this, "Potrebna je dozvola za kameru", Toast.LENGTH_SHORT).show()
        }
        else{
            startCamera()
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

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(findViewById<PreviewView>(R.id.previewView).surfaceProvider)
            }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer, imageCapture
            )
        } catch(exc: Exception) {
            Log.e("CameraX", "Use case binding failed", exc)
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

    fun startCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cameraProviderFuture.get()
            } else {
                Toast.makeText(this@MainActivity, "Potrebna je unaprijeđena verzija aplikacije za funkciju kamere", Toast.LENGTH_SHORT)
            }
            bindCameraUseCases(cameraProvider as ProcessCameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val photoFile = File(
            externalMediaDirs.first(),
            "captured-${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(this@MainActivity, "Photo saved: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                    Log.d("CameraX", "Photo saved: ${photoFile.absolutePath}")

                    runTextRecognitionOnImage (photoFile) { textList ->
                        for (text in textList) {
                            articles.add(text)
                            poveznica.notifyDataSetChanged()
                        }
                        lifecycleScope.launch{
                            val stores: List<StoreComboMatchResult> = sortStoreCombo(textList, 2, Filters.BYDISTANCE)

                            if (stores.isNotEmpty()) {
                                articles.addAll(stores.map {combo -> combo.store.joinToString(" + "){it.name} })
                                poveznica.notifyDataSetChanged()
                            } else {
                                Toast.makeText(this@MainActivity, "No store results found from recognized text", Toast.LENGTH_SHORT).show()
                            }

                            poveznica.notifyDataSetChanged()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@MainActivity, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("CameraX", "Photo capture failed", exception)
                }
            }
        )
    }

}