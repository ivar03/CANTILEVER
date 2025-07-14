package com.ivar7284.newsapp.models

data class NewsResponse(
    val totalArticles: Int,
    val articles: List<Article>
)