package com.example.shopko

import ArticleAdapter
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.entitys.Article
import com.example.shopko.entitys.ShopkoApp
import com.example.shopko.entitys.UserArticleList.articleList
import com.example.shopko.utils.repository.getArticles


class PocetnaFragment : Fragment() {

    private lateinit var listRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private val context = ShopkoApp.getAppContext()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var emptyListText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_list, container, false)
        emptyListText = view.findViewById(R.id.empty_list_text)

        val scanButton = view.findViewById<View>(R.id.btnScan)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                val intent = Intent(requireContext(), StoresActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Dozvola odbijena!", Toast.LENGTH_SHORT).show()
            }
        }

        scanButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            } else{
                MyCustomDialog{
                    articleAdapter.notifyDataSetChanged()
                    refreshListView()
                }.show(childFragmentManager, "MyCustomDialog")
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
                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    val intent = Intent(requireContext(), StoresActivity::class.java)
                    startActivity(intent)
                }
            }
            else{
                Toast.makeText(activity, "list je prazan!", Toast.LENGTH_SHORT).show()
            }
        }
        refreshListView()
        return view
    }

    private fun showAddDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Unesite naziv artikla"
            setPadding(32, 32, 32, 32)
        }

        val alertDialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Dodaj artikl")
            .setView(input)
            .setPositiveButton("Dodaj") { _, _ ->
                val newArticle = input.text.toString().trim()
                if (newArticle.isNotEmpty()) {
                    val artikl: Article? = getArticles().firstOrNull { it.type == newArticle }
                    if(artikl != null){
                        articleList.add(artikl)
                        articleAdapter.notifyDataSetChanged()
                        refreshListView()

                    }
                    else{
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

}