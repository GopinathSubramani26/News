package com.example.news.repository

import android.util.Log
import com.example.news.model.NewsModel
import com.example.news.networksource.NewsService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NewsRepository_Impl @Inject constructor(
    private val newsService: NewsService,
) : NewsRepository {

    companion object {
        const val TAG = "NewsRepository_Impl"
    }

    override suspend fun getNews(): Flow<NewsModel> = flow {

        try {
            val response = newsService.getNews()
            if (response.isSuccessful) {
                response.body()?.let { newsModel ->
                    Log.d(TAG, "$newsModel")
                    emit(newsModel)
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

