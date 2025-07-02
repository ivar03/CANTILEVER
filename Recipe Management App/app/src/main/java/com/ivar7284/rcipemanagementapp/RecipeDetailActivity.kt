package com.ivar7284.rcipemanagementapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ivar7284.rcipemanagementapp.models.Recipe
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: RecipeViewModel
    private lateinit var titleText: TextView
    private lateinit var categoryText: TextView
    private lateinit var ingredientsText: TextView
    private lateinit var instructionsText: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var ratingBar: RatingBar
    private lateinit var favoriteButton: FloatingActionButton
    
    private var recipe: Recipe? = null
    
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initViews()
        setupViewModel()
        loadRecipeDetail()
        setupClickListeners()
    }
    
    private fun initViews() {
        titleText = findViewById(R.id.titleText)
        categoryText = findViewById(R.id.categoryText)
        ingredientsText = findViewById(R.id.ingredientsText)
        instructionsText = findViewById(R.id.instructionsText)
        recipeImage = findViewById(R.id.recipeImage)
        ratingBar = findViewById(R.id.ratingBar)
        favoriteButton = findViewById(R.id.favoriteButton)
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
    }
    
    private fun loadRecipeDetail() {
        val recipeId = intent.getLongExtra("recipe_id", 0)
        
        lifecycleScope.launch {
            recipe = viewModel.getRecipeById(recipeId)
            recipe?.let { displayRecipe(it) }
        }
    }

    private fun displayRecipe(recipe: Recipe) {
        titleText.text = recipe.title
        categoryText.text = recipe.category
        ingredientsText.text = recipe.ingredients
        instructionsText.text = recipe.instructions
        ratingBar.rating = recipe.rating

        // Add debug logging to see what's happening with the image path
        android.util.Log.d("RecipeDetail", "Recipe title: ${recipe.title}")
        android.util.Log.d("RecipeDetail", "Image path: '${recipe.imagePath}'")
        android.util.Log.d("RecipeDetail", "Image path is null: ${recipe.imagePath == null}")
        android.util.Log.d("RecipeDetail", "Image path is empty: ${recipe.imagePath?.isEmpty()}")

        // Handle image loading with better error handling and logging
        if (!recipe.imagePath.isNullOrEmpty()) {
            android.util.Log.d("RecipeDetail", "Loading image with Glide: ${recipe.imagePath}")

            Glide.with(this)
                .load(recipe.imagePath)
                .centerCrop()
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        android.util.Log.e("RecipeDetail", "Glide load failed for: $model", e)
                        return false // Let Glide handle the error (show error drawable)
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        android.util.Log.d("RecipeDetail", "Glide load successful for: $model")
                        return false // Let Glide handle the success
                    }
                })
                .into(recipeImage)
        } else {
            android.util.Log.d("RecipeDetail", "No image path, using placeholder")
            recipeImage.setImageResource(R.drawable.placeholder_food)
        }

        // Set favorite icon
        favoriteButton.setImageResource(
            if (recipe.isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_outline
        )
    }
    
    private fun setupClickListeners() {
        favoriteButton.setOnClickListener {
            recipe?.let { viewModel.toggleFavorite(it) }
        }
        
        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                recipe?.let { viewModel.updateRating(it, rating) }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this, AddEditRecipeActivity::class.java)
                intent.putExtra("recipe_id", recipe?.id ?: 0)
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Recipe")
            .setMessage("Are you sure you want to delete this recipe?")
            .setPositiveButton("Delete") { _, _ ->
                recipe?.let { 
                    viewModel.deleteRecipe(it)
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}