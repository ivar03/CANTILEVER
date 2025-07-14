package com.ivar7284.newsapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivar7284.newsapp.models.Article
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadTopHeadlines()
    }

    fun loadTopHeadlines(category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTopHeadlines(category)
            _isLoading.value = false

            result.onSuccess { response ->
                _articles.value = response.articles
            }.onFailure { exception ->
                _error.value = "Failed to load news: ${exception.message}"
            }
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.searchNews(query)
            _isLoading.value = false

            result.onSuccess { response ->
                _articles.value = response.articles
            }.onFailure { exception ->
                _error.value = "Failed to search news: ${exception.message}"
            }
        }
    }
}