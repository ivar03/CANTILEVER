package com.ivar7284.rcipemanagementapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivar7284.rcipemanagementapp.database.RecipeDatabase
import com.ivar7284.rcipemanagementapp.database.RecipeRepository
import com.ivar7284.rcipemanagementapp.models.Recipe
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    val allRecipes: LiveData<List<Recipe>>
    
    init {
        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(recipeDao)
        allRecipes = repository.getAllRecipes()
    }
    
    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults
    
    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory
    
    fun insertRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.insertRecipe(recipe)
    }
    
    fun updateRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.updateRecipe(recipe)
    }
    
    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch {
        repository.deleteRecipe(recipe)
    }
    
    fun toggleFavorite(recipe: Recipe) = viewModelScope.launch {
        repository.updateFavoriteStatus(recipe.id, !recipe.isFavorite)
    }
    
    fun updateRating(recipe: Recipe, rating: Float) = viewModelScope.launch {
        repository.updateRating(recipe.id, rating)
    }
    
    fun searchRecipes(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            repository.searchRecipes(query).observeForever { results ->
                _searchResults.value = results
            }
        }
    }
    
    fun getRecipesByCategory(category: String): LiveData<List<Recipe>> {
        _selectedCategory.value = category
        return repository.getRecipesByCategory(category)
    }
    
    fun getFavoriteRecipes(): LiveData<List<Recipe>> = repository.getFavoriteRecipes()
    
    suspend fun getRecipeById(id: Long): Recipe? = repository.getRecipeById(id)
}
