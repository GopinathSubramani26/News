package com.example.news.repository

import com.example.news.model.NewsModel
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    suspend fun getNews(): Flow<NewsModel>

}

