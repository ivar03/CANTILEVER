package com.ivar7284.newsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var chipGroup: ChipGroup
    private lateinit var searchView: SearchView
    private lateinit var newsAdapter: NewsAdapter
    private val viewModel: NewsViewModel by viewModels()

    private val categories = listOf(
        "general", "business", "entertainment", "health",
        "science", "sports", "technology"
    )

    private var isSearchMode = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        setupRecyclerView()
        setupChips()
        setupSearchView()
        observeViewModel()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        chipGroup = findViewById(R.id.chipGroup)
        searchView = findViewById(R.id.searchView)

        swipeRefreshLayout.setOnRefreshListener {
            if (isSearchMode) {
                // If in search mode, perform the last search again
                val currentQuery = searchView.query.toString()
                if (currentQuery.isNotEmpty()) {
                    viewModel.searchNews(currentQuery)
                } else {
                    viewModel.loadTopHeadlines()
                    isSearchMode = false
                }
            } else {
                viewModel.loadTopHeadlines()
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter { article ->
            openArticle(article.url)
        }

        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupChips() {
        // Add "All" chip
        val allChip = Chip(this)
        allChip.text = "All"
        allChip.isCheckable = true
        allChip.isChecked = true
        allChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !isSearchMode) {
                viewModel.loadTopHeadlines()
            }
        }
        chipGroup.addView(allChip)

        // Add category chips
        categories.forEach { category ->
            val chip = Chip(this)
            chip.text = category.replaceFirstChar { it.uppercase() }
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && !isSearchMode) {
                    viewModel.loadTopHeadlines(category)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchQuery ->
                    if (searchQuery.isNotEmpty()) {
                        isSearchMode = true
                        viewModel.searchNews(searchQuery)
                        // Clear chip selections when searching
                        chipGroup.clearCheck()
                        // Hide keyboard
                        searchView.clearFocus()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // If search text is cleared, go back to top headlines
                if (newText.isNullOrEmpty() && isSearchMode) {
                    isSearchMode = false
                    viewModel.loadTopHeadlines()
                    // Restore "All" chip selection
                    val allChip = chipGroup.getChildAt(0) as? Chip
                    allChip?.isChecked = true
                }
                return true
            }
        })

        // Handle search view close button
        searchView.setOnCloseListener {
            if (isSearchMode) {
                isSearchMode = false
                viewModel.loadTopHeadlines()
                // Restore "All" chip selection
                val allChip = chipGroup.getChildAt(0) as? Chip
                allChip?.isChecked = true
            }
            false
        }
    }

    private fun observeViewModel() {
        viewModel.articles.observe(this) { articles ->
            newsAdapter.updateArticles(articles)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openArticle(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                if (isSearchMode) {
                    val currentQuery = searchView.query.toString()
                    if (currentQuery.isNotEmpty()) {
                        viewModel.searchNews(currentQuery)
                    } else {
                        viewModel.loadTopHeadlines()
                        isSearchMode = false
                    }
                } else {
                    viewModel.loadTopHeadlines()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}