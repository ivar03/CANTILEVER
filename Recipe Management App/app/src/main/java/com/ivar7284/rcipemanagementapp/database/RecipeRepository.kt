package com.ivar7284.rcipemanagementapp.database

import androidx.lifecycle.LiveData
import com.ivar7284.rcipemanagementapp.models.Recipe

class RecipeRepository(private val recipeDao: RecipeDao) {
    
    fun getAllRecipes(): LiveData<List<Recipe>> = recipeDao.getAllRecipes()
    
    fun getRecipesByCategory(category: String): LiveData<List<Recipe>> = 
        recipeDao.getRecipesByCategory(category)
    
    fun getFavoriteRecipes(): LiveData<List<Recipe>> = recipeDao.getFavoriteRecipes()
    
    fun searchRecipes(query: String): LiveData<List<Recipe>> = recipeDao.searchRecipes(query)
    
    suspend fun getRecipeById(id: Long): Recipe? = recipeDao.getRecipeById(id)
    
    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)
    
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
    
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean) = 
        recipeDao.updateFavoriteStatus(id, isFavorite)
    
    suspend fun updateRating(id: Long, rating: Float) = recipeDao.updateRating(id, rating)
}