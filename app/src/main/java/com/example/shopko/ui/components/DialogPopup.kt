package com.example.shopko.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.shopko.R
import com.example.shopko.data.model.ShopkoApp
import com.example.shopko.data.model.UserArticleList.articleList
import com.example.shopko.utils.FileUtils
import com.example.shopko.data.repository.getArticles
import com.example.shopko.utils.camera.runTextRecognitionOnImage
import java.io.File

class MyCustomDialog(private val onArticlesAdded: () -> Unit) : DialogFragment() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var previewView: PreviewView
    private lateinit var takePhotoButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var openGalleryButton: ImageButton
    private val context = ShopkoApp.getAppContext()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = FileUtils.getFileFromUri(requireContext(), it)
            runTextRecognitionOnImage(file) { scannedArticlesList ->
                for (text in scannedArticlesList) {
                    Log.d("TextRecognition", "Recognized text from gallery: $text")
                }

                if (scannedArticlesList.isNotEmpty()) {
                    articleList.addAll(scannedArticlesList)
                    onArticlesAdded()
                    dialog?.dismiss()
                } else {
                    Toast.makeText(requireContext(), "No store results found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_camera, container, false)
        previewView = view.findViewById(R.id.previewView)
        takePhotoButton = view.findViewById(R.id.captureButton)
        backButton = view.findViewById(R.id.btnBack)
        openGalleryButton = view.findViewById(R.id.btnImg)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera()

        takePhotoButton.setOnClickListener {
            takePhoto()
        }

        backButton.setOnClickListener {
            dialog?.dismiss()
        }

        openGalleryButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.insetsController?.apply {
            hide(WindowInsets.Type.systemBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun startCamera() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .build()
                        .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer, imageCapture)
                } catch (exc: Exception) {
                    Log.e("CameraX", "Failed to bind camera use cases", exc)
                }
            }, ContextCompat.getMainExecutor(requireContext()))
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takePhoto() {
        val photoFile = File(
            requireContext().getExternalFilesDir(null),
            "captured-${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(context, "Photo saved: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                    Log.d("CameraX", "Photo saved: ${photoFile.absolutePath}")

                    runTextRecognitionOnImage(photoFile) { scannedArticlesList ->
                        for (text in scannedArticlesList) {
                            Log.d("TextRecognition", "Recognized text: $text")
                        }

                        if (scannedArticlesList.isNotEmpty()) {
                            articleList.addAll(scannedArticlesList)
                            onArticlesAdded()
                            Log.d("ARTIKLI", "$articleList")
                        } else {
                            Toast.makeText(context, "No store results found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog?.dismiss()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("CameraX", "Photo capture failed", exception)
                }
            }
        )
    }
}
