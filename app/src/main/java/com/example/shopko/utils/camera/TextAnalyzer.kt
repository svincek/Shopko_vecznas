package com.example.shopko.utils.camera
import android.net.Uri
import android.util.Log
import com.example.shopko.data.model.Article
import com.example.shopko.data.model.ShopkoApp
import com.example.shopko.data.repository.getArticles
import com.example.shopko.utils.general.levenshtein
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.util.Locale

/**
 * Pokreće prepoznavanje teksta iz slike koristeći Google ML Kit te pokušava prepoznati artikle iz baze podataka
 * uspoređujući prepoznati tekst s poznatim tipovima artikala.
 *
 * @param imageFile Datoteka slike iz koje će se čitati tekst.
 * @param onResult Povratni rezultat u obliku lambda funkcije koja prima listu prepoznatih artikala.
 *
 * Funkcija koristi:
 * - ML Kit za optičko prepoznavanje teksta (OCR)
 * - Levenshteinovu udaljenost za usporedbu sličnosti između prepoznatog teksta i tipova artikala
 *
 * Prepoznati tekst se normalizira (uklanjaju se praznine i pretvara se u velika slova), a zatim se
 * uspoređuje s nazivima tipova artikala iz baze. Ako je Levenshteinova udaljenost manja ili jednaka 3,
 * artikl se smatra prepoznatim.
 *
 * U slučaju greške pri učitavanju slike ili neuspjeha prepoznavanja, vraća se prazna lista.
 */

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

                val products: List<Article> = getArticles()

                val cleanedArticleList = mutableListOf<Article>()

                for(text in textList) {
                    for(product in products) {
                        val distance = levenshtein(text, product.type.replace("\\s+".toRegex(), ""))

                        if (distance <= 3) {
                            cleanedArticleList.add(product)
                            break
                        }
                    }
                }
                onResult(cleanedArticleList.distinct())
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
