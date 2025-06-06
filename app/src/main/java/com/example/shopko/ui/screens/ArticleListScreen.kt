package com.example.shopko.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.Article
import com.example.shopko.data.model.ShopkoApp
import com.example.shopko.data.model.UserArticleList.articleList
import com.example.shopko.data.remote.ApiService
import com.example.shopko.data.repository.AppDatabase
import com.example.shopko.data.repository.getArticles
import com.example.shopko.ui.adapters.ArticleAdapter
import com.example.shopko.ui.components.MyCustomDialog
import kotlinx.coroutines.launch


class PocetnaFragment : Fragment() {

    private lateinit var listRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var emptyListText: TextView
    private var permissionForScan = false
    private val db = AppDatabase.getDatabase(ShopkoApp.getAppContext())
    private val api = ApiService()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_list, container, false)
        emptyListText = view.findViewById(R.id.empty_list_text)

        lifecycleScope.launch {
            val article = db.articleDao().getAllArticles().first()
            val rarticle = Article(article.productId.toInt(), article.name, article.brand,
                article.category.toString(),
                article.unit.toString(), article.price)
            articleList.add(rarticle)
            articleAdapter.notifyDataSetChanged()
        }

        val scanButton = view.findViewById<View>(R.id.btnScan)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                if (permissionForScan) {
                    showScanDialog()
                } else {
                    val intent = Intent(requireContext(), StoresScreen::class.java)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(requireContext(), "Dozvola je potrebna za neke funckionalnosti!", Toast.LENGTH_SHORT).show()
            }
        }

        scanButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionForScan = true
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else{
                showScanDialog()
            }
        }
        listRecyclerView = view.findViewById(R.id.list)
        val addButton: ImageButton = view.findViewById(R.id.btnAdd)

        articleAdapter = ArticleAdapter(articleList)
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        listRecyclerView.adapter = articleAdapter

        addButton.setOnClickListener {
            showAddDialog()
        }
        val btnAdvance = view.findViewById<Button>(R.id.advance)
        btnAdvance.setOnClickListener {
            if(articleList.isNotEmpty()){
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionForScan = false
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    val intent = Intent(requireContext(), StoresScreen::class.java)
                    startActivity(intent)
                }
            }
            else{
                Toast.makeText(activity, "lista je prazna!", Toast.LENGTH_SHORT).show()
            }
        }
        refreshListView()
        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAddDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Unesite naziv artikla"
            setPadding(32, 32, 32, 32)
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Dodaj artikl")
            .setView(input)
            .setPositiveButton("Dodaj") { _, _ ->
                val newArticle = input.text.toString().trim().uppercase()
                if (newArticle.isNotEmpty()) {
                    val artikl: Article? = getArticles().firstOrNull { it.type.uppercase() == newArticle }
                    if (artikl != null) {
                        articleList.add(artikl)
                        articleAdapter.notifyDataSetChanged()
                        refreshListView()
                    } else {
                        Toast.makeText(activity, "Nije pronađen artikl: $newArticle", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Odustani", null)
            .create()

        alertDialog.show()
    }

    private fun refreshListView() {
        if (articleList.isEmpty()) {
            emptyListText.visibility = View.VISIBLE
            listRecyclerView.visibility = View.GONE
        } else {
            emptyListText.visibility = View.GONE
            listRecyclerView.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showScanDialog() {
        MyCustomDialog {
            articleAdapter.notifyDataSetChanged()
            refreshListView()
        }.show(childFragmentManager, "MyCustomDialog")
    }

}