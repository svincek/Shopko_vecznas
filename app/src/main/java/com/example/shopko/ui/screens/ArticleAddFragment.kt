package com.example.shopko.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.Article
import com.example.shopko.data.model.UserArticleList.articleList
import com.example.shopko.data.repository.getArticles
import com.example.shopko.ui.adapters.ArticleSelectAdapter

class ArticleAddFragment : Fragment() {

    private lateinit var adapter: ArticleSelectAdapter
    private lateinit var placeholder: ImageView
    private lateinit var resultCount: TextView
    private lateinit var recyclerViewArticles: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var searchBar: EditText
    private lateinit var btnAdd: ImageButton
    private lateinit var textResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_add, container, false)

        btnBack = view.findViewById(R.id.btnBack)
        searchBar = view.findViewById(R.id.searchBar)
        btnAdd = view.findViewById(R.id.btnAdd)
        placeholder = view.findViewById(R.id.placeholder)
        resultCount = view.findViewById(R.id.resultCount)
        recyclerViewArticles = view.findViewById(R.id.recyclerViewArticles)
        textResult = view.findViewById(R.id.textResult)

        adapter = ArticleSelectAdapter(emptyList())
        recyclerViewArticles.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewArticles.adapter = adapter
        recyclerViewArticles.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )

        updateUI(emptyList())

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().uppercase()

                val filtered = getArticles()
                    .filter { it.type.uppercase().contains(query) }
                    .distinctBy { it.type.uppercase() }
                    .sortedBy { it.type.uppercase().indexOf(query) }
                    .take(10)

                adapter.updateList(filtered)
                updateUI(filtered)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnAdd.setOnClickListener {
            val selected = adapter.getSelectedItems()
            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), "Niste odabrali nijedan artikl!", Toast.LENGTH_SHORT).show()
            } else {
                selected.forEach { newArticle ->
                    if (!articleList.any { it.type == newArticle.type }) {
                        articleList.add(newArticle)
                    }
                }
                Toast.makeText(requireContext(), "Več dodano ${selected.size} artikala", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    private fun updateUI(filteredList: List<Article>) {
        if (filteredList.isEmpty()) {
            Log.d("MAMICA", filteredList.isEmpty().toString())
            recyclerViewArticles.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
            resultCount.visibility = View.GONE
            textResult.visibility = View.GONE
        } else {
            Log.d("MAMICA", filteredList.isEmpty().toString())
            recyclerViewArticles.visibility = View.VISIBLE
            resultCount.visibility = View.VISIBLE
            placeholder.visibility = View.GONE
            textResult.visibility = View.VISIBLE
        }

        resultCount.text = "${filteredList.size} rezultata"
    }
}
