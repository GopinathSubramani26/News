package com.example.news.networksource

import com.example.news.model.NewsModel
import retrofit2.Response
import retrofit2.http.GET

interface NewsService {

    @GET("Android/news-api-feed/staticResponse.json")
    suspend fun getNews():Response<NewsModel>

}