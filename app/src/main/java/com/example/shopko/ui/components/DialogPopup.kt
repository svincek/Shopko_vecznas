package com.example.shopko.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.shopko.R
import com.example.shopko.data.model.ShopkoApp
import com.example.shopko.data.model.UserArticleList.articleList
import com.example.shopko.data.repository.getArticles
import com.example.shopko.utils.camera.runTextRecognitionOnImage
import java.io.File


class MyCustomDialog(private val onArticlesAdded: () -> Unit) : DialogFragment() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var previewView: PreviewView
    private lateinit var takePhotoButton: Button
    private val context = ShopkoApp.getAppContext()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_camera, container, false)
        previewView = view.findViewById(R.id.previewView)
        takePhotoButton = view.findViewById(R.id.captureButton)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCamera()

        takePhotoButton.setOnClickListener {
            takePhoto()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(900, 2200)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
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
                    context.let {
                        Toast.makeText(it, "Photo saved: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("CameraX", "Photo saved: ${photoFile.absolutePath}")

                    runTextRecognitionOnImage(photoFile) { scannedArticlesList ->
                        for (text in scannedArticlesList) {
                            Log.d("TextRecognition", "Recognized text: $text")
                        }

                        Log.d("SKEN", "$scannedArticlesList")
                        if (scannedArticlesList.isNotEmpty()) {
                                articleList.addAll(scannedArticlesList.map { article ->
                                    getArticles().first { it.type == article }
                                }.toList())
                                onArticlesAdded()
                                Log.d("ARTIKLI", "$articleList")
                            }
                            else {
                                context.let {
                                    Toast.makeText(it, "No store results found", Toast.LENGTH_SHORT).show()
                                }
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

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Permission denied for camera", Toast.LENGTH_SHORT).show()
        }
    }
}