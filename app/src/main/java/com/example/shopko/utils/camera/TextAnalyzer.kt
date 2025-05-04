package com.example.shopko.utils.camera
import android.net.Uri
import android.util.Log
import com.example.shopko.entitys.Article
import com.example.shopko.entitys.ShopkoApp
import com.example.shopko.utils.general.levenshtein
import com.example.shopko.utils.repository.getArticles
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.util.Locale

fun runTextRecognitionOnImage(imageFile: File, onResult: (List<Article>) -> Unit) {
    val context = ShopkoApp.getAppContext()
    try {
        val inputImage = InputImage.fromFilePath(context, Uri.fromFile(imageFile))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val textList = visionText.textBlocks
                    .map { it.text }
                    .map {it.replace("\\s+".toRegex(), "")}
                    .map { it.uppercase(Locale.getDefault()) }
                    .filter { it.isNotBlank() }
                Log.d("CapturedText", "Recognized text: $textList")

                val products: List<Article> = getArticles()

                val cleanedTextList = mutableListOf<Article>()

                for(text in textList) {
                    for(product in products) {
                        val distance = levenshtein(text, product.type.replace("\\s+".toRegex(), ""))

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
