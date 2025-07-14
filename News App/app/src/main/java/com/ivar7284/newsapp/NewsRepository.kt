package com.ivar7284.newsapp

import android.util.Log
import com.ivar7284.newsapp.models.NewsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsRepository {
    private val api: NewsApiService
    private val apiKey = "a0dcbdbac94abfd94d32b3ff83b3fe8d"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://gnews.io/api/v4/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NewsApiService::class.java)
    }

    suspend fun getTopHeadlines(category: String? = null): Result<NewsResponse> {
        return try {
            Log.d("NewsRepository", "Calling GNews API for top headlines with category: $category")

            val response = api.getTopHeadlines(
                category = category ?: "general",
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("NewsRepository", "Fetched ${it.articles.size} articles from ${it.totalArticles} total")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = "HTTP ${response.code()}: ${response.message()}"
                Log.e("NewsRepository", "API Error: $errorMsg\n$errorBody")

                Result.failure(Exception("API error: $errorMsg"))
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun searchNews(query: String): Result<NewsResponse> {
        return try {
            Log.d("NewsRepository", "Searching news: $query")

            val response = api.searchNews(
                query = query,
                apiKey = apiKey
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("NewsRepository", "Search returned ${it.articles.size} articles from ${it.totalArticles} total")
                    Result.success(it)
                } ?: Result.failure(Exception("Empty search response"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = "HTTP ${response.code()}: ${response.message()}"
                Log.e("NewsRepository", "Search Error: $errorMsg\n$errorBody")

                Result.failure(Exception("Search failed: $errorMsg"))
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception during search: ${e.message}")
            Result.failure(e)
        }
    }
}