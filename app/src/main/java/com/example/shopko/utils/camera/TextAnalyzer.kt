package com.example.shopko.utils.camera
import android.net.Uri
import android.util.Log
import com.example.shopko.data.model.entitys.ShopkoApp
import com.example.shopko.utils.general.levenshtein
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException

fun runTextRecognitionOnImage(products: List<String>, imageFile: File, onResult: (List<String>) -> Unit) {
    val context = ShopkoApp.getAppContext()
    try {
        val inputImage = InputImage.fromFilePath(context, Uri.fromFile(imageFile))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val textList = visionText.textBlocks
                    .map { it.text }
                    .map {it.replace("\\s+".toRegex(), "")}
                    .map { it.uppercase() }
                    .filter { it.isNotBlank() }
                Log.d("CapturedText", "Recognized text: $textList")

                val cleanedTextList = mutableListOf<String>()

                for(text in textList) {
                    for(product in products) {
                        val distance = levenshtein(text, product.replace("\\s+".toRegex(), "").uppercase())
                        if (distance <= 3) {
                            cleanedTextList.add(product)
                        }
                    }
                }
                onResult(cleanedTextList.distinct())
            }
            .addOnFailureListener { e ->
                Log.e("CapturedText", "Text recognition failed", e)
                onResult(emptyList())
            }

    } catch (e: IOException) {
        Log.e("CapturedText", "Failed to load image file", e)
        onResult(emptyList())
    }
}
