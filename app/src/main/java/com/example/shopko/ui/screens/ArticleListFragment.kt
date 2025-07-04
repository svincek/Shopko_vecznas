package com.example.shopko.ui.screens

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopko.R
import com.example.shopko.data.model.objects.UserArticleList.articleList
import com.example.shopko.data.repository.AppDatabase
import com.example.shopko.ui.adapters.ArticleAdapter
import com.example.shopko.ui.components.MyCustomDialog
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ArticleListFragment : Fragment() {

    private lateinit var listRecyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var emptyListText: TextView
    private lateinit var emptyListImg: ImageView
    private lateinit var emptyListTextUnder: TextView
    private lateinit var btnAdvance: ImageButton
    private lateinit var cardMain: CardView
    private var permissionForScan = false
    private lateinit var cardBtnUp: CardView
    private lateinit var btnToTop: ImageButton
    private lateinit var btnExport: ImageButton
    private lateinit var searchBar: EditText
    private lateinit var myListText: TextView
    private lateinit var db: AppDatabase
    private var hasLoadedFromDb: Boolean = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_list, container, false)
        listRecyclerView = view.findViewById(R.id.list)
        emptyListText = view.findViewById(R.id.empty_list_text)
        emptyListImg = view.findViewById(R.id.imageViewEmpty)
        emptyListTextUnder = view.findViewById(R.id.underEmptyListText)
        btnAdvance = view.findViewById(R.id.btnAdvance)
        cardMain = view.findViewById(R.id.cardViewMain)
        cardBtnUp = view.findViewById(R.id.btnTopCard)
        btnToTop = view.findViewById(R.id.btnScrollToTop)
        btnExport = view.findViewById(R.id.btnExport)
        searchBar = view.findViewById(R.id.searchBar)
        myListText = view.findViewById(R.id.myListText)
        db = AppDatabase.getDatabase(requireContext())
        articleAdapter = ArticleAdapter(articleList, emptyList())
        listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        listRecyclerView.adapter = articleAdapter

        searchBar.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                articleAdapter.filter(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        lifecycleScope.launch {
            if(!hasLoadedFromDb){
                val dao = db.userArticlesDao()
                val articles = dao.getAll().map { it.toDisplay() }

                articleList.clear()
                articleList.addAll(articles)

                articleAdapter.notifyDataSetChanged()
                refreshListView()
                hasLoadedFromDb = true
            }
        }

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

        lifecycleScope.launch {
            val favouriteArticles = db.articleDao().getFavouriteArticles()

            articleAdapter = ArticleAdapter(articleList, favouriteArticles)
            listRecyclerView.adapter = articleAdapter
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removedItem = articleList[position]

                articleList.removeAt(position)
                articleAdapter.notifyItemRemoved(position)
                refreshListView()

                val snackbar = Snackbar.make(listRecyclerView, "Artikl uklonjen s popisa", Snackbar.LENGTH_LONG)
                snackbar.setAction("Poništi") {
                    articleList.add(position, removedItem)
                    articleAdapter.notifyItemInserted(position)
                    refreshListView()
                }



                val snackbarView = snackbar.view
                val snackbarText = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                val snackbarAction = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)


                val background = snackbarView.background as? MaterialShapeDrawable
                background?.shapeAppearanceModel = background.shapeAppearanceModel
                    .toBuilder()
                    .setAllCornerSizes(20f)
                    .build()


                background?.setTint(ContextCompat.getColor(requireContext(), R.color.black))


                snackbarText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_smoke))
                snackbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                snackbarText.typeface = ResourcesCompat.getFont(requireContext(), R.font.satoshi_medium)

                snackbarAction.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_red))
                snackbarAction.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                snackbarAction.typeface = ResourcesCompat.getFont(requireContext(), R.font.satoshi_medium)

                snackbar.show()

            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                val background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 30f
                    setColor(Color.parseColor("#c02424"))
                }

                val icon = ContextCompat.getDrawable(requireContext(), R.drawable.icon_x)
                val iconMargin = (itemView.height - (icon?.intrinsicHeight ?: 0)) / 2

                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + (icon?.intrinsicHeight ?: 0)
                val iconLeft = itemView.right - iconMargin - (icon?.intrinsicWidth ?: 0)
                val iconRight = itemView.right - iconMargin

                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                icon?.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })
        itemTouchHelper.attachToRecyclerView(listRecyclerView)




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

        btnExport.setOnClickListener {
            exportArticleListToTxt()
        }

        btnToTop.setOnClickListener {
            listRecyclerView.smoothScrollToPosition(0)
        }

        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_add_article)
        }

        btnAdvance.setOnClickListener {
            lifecycleScope.launch {
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
                        val db = AppDatabase.getDatabase(requireContext())
                        val dao = db.userArticlesDao()
                        dao.clearAll()
                        dao.insertAll(selectedArticles.map { it.toEntity() })
                        findNavController().navigate(R.id.action_storesFragment)
                    }
                } else {
                    Toast.makeText(activity, "Odaberite barem jedan artikl!", Toast.LENGTH_SHORT).show()
                }
            }
        }


        refreshListView()
        return view
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val updatedFavourites = db.articleDao().getFavouriteArticles()
            articleAdapter.updateFavourites(updatedFavourites)
        }
    }

    @SuppressLint("NotifyDataSetChanged")


    private fun refreshListView() {
        if (articleList.isEmpty()) {
            emptyListText.visibility = View.VISIBLE
            emptyListImg.visibility = View.VISIBLE
            emptyListTextUnder.visibility = View.VISIBLE
            btnAdvance.setImageResource(R.drawable.button_grayed_out)
            listRecyclerView.visibility = View.GONE
            btnExport.visibility = View.GONE
            searchBar.visibility = View.GONE
            myListText.visibility = View.GONE

        } else {
            cardMain.layoutParams.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                750.toFloat(),
                resources.displayMetrics
            ).toInt()
            emptyListText.visibility = View.GONE
            emptyListImg.visibility = View.GONE
            emptyListTextUnder.visibility = View.GONE
            btnAdvance.visibility = View.VISIBLE
            listRecyclerView.visibility = View.VISIBLE
            btnAdvance.setImageResource(R.drawable.button_advance)
            btnExport.visibility = View.VISIBLE
            searchBar.visibility = View.VISIBLE
            myListText.visibility = View.VISIBLE
        }
    }

    private fun showScanDialog() {
        MyCustomDialog {
            articleAdapter.notifyDataSetChanged()
            refreshListView()
            listRecyclerView.adapter = articleAdapter
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
    private fun exportArticleListToTxt() {
        if (articleList.isEmpty()) {
            Toast.makeText(requireContext(), "Popis je prazan!", Toast.LENGTH_SHORT).show()
            return
        }

        val exportText = articleList.joinToString("\n") { "${it.subcategory} - ${it.buyQuantity} kom" }

        try {
            val exportDir = File(requireContext().getExternalFilesDir(null), "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, "MojPopis.txt")
            val writer = FileWriter(file)
            writer.write(exportText)
            writer.flush()
            writer.close()


            val uri: Uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Pošalji popis"))

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Greška pri spremanju datoteke", Toast.LENGTH_SHORT).show()
        }
    }
}
