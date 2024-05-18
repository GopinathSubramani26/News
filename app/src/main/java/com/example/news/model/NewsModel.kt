package com.example.news.model

data class NewsModel(
    val articles: List<Article>?,
    val status: String?
)

data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)

data class Source(
    val id: String?,
    val name: String?
)

data class NewsArticle(
    val id: String?,
    val urlToImage: String?,
    val author: String?,
    val title: String?,
    val publishedAt: String?,
    val url: String?
)