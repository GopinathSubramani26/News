package com.example.news.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.model.NewsArticle
import com.example.news.repository.NewsRepository_Impl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class NewsState {
    object Loading : NewsState()
    object Success : NewsState()
    object Error : NewsState()
    object None : NewsState()
}

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository_Impl,
) : ViewModel() {

    companion object {
        private const val TAG = "NewsViewModel"
    }

    val newsState = mutableStateOf<NewsState>(NewsState.None)

    private val _newsArticle = MutableStateFlow<List<NewsArticle?>>(emptyList())
    val newsArticle: StateFlow<List<NewsArticle?>> = _newsArticle

    private var isDescending = true

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchNews(){
        newsState.value = NewsState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getNews()
                    .distinctUntilChanged()
                    .collect { newsModel ->
                       val newsArticle = newsModel.articles?.map { article ->
                           NewsArticle(
                               id = article.source?.id,
                               urlToImage = article.urlToImage,
                               author = article.author?:"",
                               title = article.title?:"",
                               publishedAt = article.publishedAt?:"",
                               url = article.url?:""
                           )
                       }?.sortedByDescending { article ->
                           ZonedDateTime.parse(article.publishedAt, DateTimeFormatter.ISO_DATE_TIME)
                       }
                        _newsArticle.value = newsArticle ?: emptyList()
                    }
                newsState.value = NewsState.Success

            } catch (e: Exception) {
                Log.e(TAG, "Fetch News Failed: ${e.message}")
                newsState.value = NewsState.Error
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sortList() {
        val sortedList = if(isDescending) {
            _newsArticle.value.filterNotNull().sortedByDescending { article ->
                ZonedDateTime.parse(article.publishedAt, DateTimeFormatter.ISO_DATE_TIME)
            }
        }else {
            _newsArticle.value.filterNotNull().sortedBy { article ->
            ZonedDateTime.parse(article.publishedAt, DateTimeFormatter.ISO_DATE_TIME)
        }
        }
        _newsArticle.value = sortedList
       isDescending  = !isDescending
    }
}
