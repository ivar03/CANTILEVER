package com.ivar7284.newsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var newsAdapter: NewsAdapter
    private val viewModel: NewsViewModel by viewModels()

    private val categories = listOf(
        "general", "business", "entertainment", "health",
        "science", "sports", "technology"
    )

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
        observeViewModel()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        chipGroup = findViewById(R.id.chipGroup)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadTopHeadlines()
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
            if (isChecked) {
                viewModel.loadTopHeadlines()
            }
        }
        chipGroup.addView(allChip)

        // Add category chips
        categories.forEach { category ->
            val chip = Chip(this)
            chip.text = category.capitalize()
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.loadTopHeadlines(category)
                }
            }
            chipGroup.addView(chip)
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

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchNews(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.loadTopHeadlines()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}