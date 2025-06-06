package com.example.shopko.ui.screens

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.Article
import com.example.shopko.data.model.UserArticleList.articleList
import com.example.shopko.data.repository.getArticles
import com.example.shopko.ui.adapters.ArticleAdapter
import com.example.shopko.ui.components.MyCustomDialog

class ArticleListFragment : Fragment() {

    private lateinit var listRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var emptyListText: TextView
    private lateinit var emptyListImg: ImageView
    private lateinit var emptyListTextUnder: TextView
    private lateinit var cardAdvance: CardView
    private lateinit var cardMain: CardView
    private var permissionForScan = false
    private lateinit var cardBtnUp: CardView
    private lateinit var btnToTop: ImageButton

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_list, container, false)
        emptyListText = view.findViewById(R.id.empty_list_text)
        emptyListImg = view.findViewById(R.id.imageViewEmpty)
        emptyListTextUnder = view.findViewById(R.id.underEmptyListText)
        cardAdvance = view.findViewById(R.id.cardAdvance)
        cardMain = view.findViewById(R.id.cardViewMain)
        cardBtnUp = view.findViewById(R.id.btnTopCard)
        btnToTop = view.findViewById(R.id.btnScrollToTop)


        val scanButton = view.findViewById<View>(R.id.btnScan)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                if (permissionForScan) {
                    showScanDialog()
                } else {
                    findNavController().navigate(R.id.storesFragment)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Dozvola je potrebna za neke funkcionalnosti!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        scanButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionForScan = true
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                showScanDialog()
            }
        }

        listRecyclerView = view.findViewById(R.id.list)
        val addButton: ImageButton = view.findViewById(R.id.btnAdd)

        articleAdapter = ArticleAdapter(articleList)
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        listRecyclerView.adapter = articleAdapter

        var cardIsShrunk = false

        listRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var isButtonVisible = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val totalItems = articleAdapter.itemCount

                if (totalItems > 7) {
                    if (!isButtonVisible && lastVisible >= 10) {
                        isButtonVisible = true
                        cardBtnUp.apply {
                            visibility = View.VISIBLE
                            alpha = 0f
                            animate().alpha(1f).setDuration(300).start()
                        }
                    }
                    val isAtBottom = lastVisibleItem == totalItems - 1
                    if (isAtBottom && !cardIsShrunk) {
                        val currentHeight = cardMain.height
                        val targetHeight = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            500f,
                            resources.displayMetrics
                        ).toInt()

                        animateCardHeight(currentHeight, targetHeight)
                        cardIsShrunk = true

                    } else if (!isAtBottom && cardIsShrunk) {
                        val currentHeight = cardMain.height
                        val originalHeight = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            750f,
                            resources.displayMetrics
                        ).toInt()

                        animateCardHeight(currentHeight, originalHeight)
                        cardIsShrunk = false
                    }

                    if (isButtonVisible && lastVisible < 10) {
                        isButtonVisible = false
                        cardBtnUp.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction {
                                cardBtnUp.visibility = View.GONE
                            }
                            .start()
                    }
                }
            }
        })


        btnToTop.setOnClickListener {
            listRecyclerView.smoothScrollToPosition(0)
        }




        addButton.setOnClickListener {
            showAddDialog()
        }

        val btnAdvance = view.findViewById<ImageButton>(R.id.advance)
        btnAdvance.setOnClickListener {
            val selectedArticles = articleList.filter { it.isChecked }
            if (selectedArticles.isNotEmpty()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionForScan = false
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    findNavController().navigate(R.id.action1)
                }
            } else {
                Toast.makeText(activity, "Odaberite barem jedan artikl!", Toast.LENGTH_SHORT).show()
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
            emptyListImg.visibility = View.VISIBLE
            emptyListTextUnder.visibility = View.VISIBLE
            cardAdvance.visibility = View.GONE
            listRecyclerView.visibility = View.GONE

        } else {
            cardMain.layoutParams.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                750.toFloat(),
                resources.displayMetrics
            ).toInt()
            emptyListText.visibility = View.GONE
            emptyListImg.visibility = View.GONE
            emptyListTextUnder.visibility = View.GONE
            cardAdvance.visibility = View.VISIBLE
            listRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showScanDialog() {
        MyCustomDialog {
            articleAdapter.notifyDataSetChanged()
            refreshListView()
        }.show(childFragmentManager, "MyCustomDialog")
    }
    private fun animateCardHeight(fromHeight: Int, toHeight: Int) {
        val animator = ValueAnimator.ofInt(fromHeight, toHeight)
        animator.duration = 300
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = cardMain.layoutParams
            layoutParams.height = animatedValue
            cardMain.layoutParams = layoutParams
        }
        animator.start()
    }
}
