package com.ivar7284.newsapp

import com.ivar7284.newsapp.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String? = "general",
        @Query("country") country: String = "us",
        @Query("lang") lang: String = "en",
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String
    ): Response<NewsResponse>

    @GET("search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("lang") lang: String = "en",
        @Query("country") country: String = "us",
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String
    ): Response<NewsResponse>
}