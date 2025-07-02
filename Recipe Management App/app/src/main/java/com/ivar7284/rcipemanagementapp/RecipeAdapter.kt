package com.ivar7284.rcipemanagementapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ivar7284.rcipemanagementapp.models.Recipe

class RecipeAdapter(
    private val onRecipeClick: (Recipe) -> Unit,
    private val onFavoriteClick: (Recipe) -> Unit,
    private val onRatingChange: (Recipe, Float) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    
    private var recipes = emptyList<Recipe>()
    
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleText)
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val ingredientsPreview: TextView = itemView.findViewById(R.id.ingredientsPreview)
        val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(itemView)
    }
    
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        
        holder.titleText.text = recipe.title
        holder.categoryText.text = recipe.category
        holder.ingredientsPreview.text = recipe.ingredients.take(100) + "..."
        holder.ratingBar.rating = recipe.rating
        
        // Load image with Glide
        if (!recipe.imagePath.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(recipe.imagePath)
                .centerCrop()
                .placeholder(R.drawable.placeholder_food)
                .into(holder.recipeImage)
        } else {
            holder.recipeImage.setImageResource(R.drawable.placeholder_food)
        }
        
        // Set favorite icon
        holder.favoriteButton.setImageResource(
            if (recipe.isFavorite) R.drawable.ic_favorite_filled 
            else R.drawable.ic_favorite_outline
        )
        
        // Click listeners
        holder.itemView.setOnClickListener { onRecipeClick(recipe) }
        holder.favoriteButton.setOnClickListener { onFavoriteClick(recipe) }
        holder.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) onRatingChange(recipe, rating)
        }
    }
    
    override fun getItemCount() = recipes.size
    
    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}